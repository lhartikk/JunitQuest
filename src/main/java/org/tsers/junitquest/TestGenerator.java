package org.tsers.junitquest;

import org.objectweb.asm.tree.MethodNode;
import org.tsers.junitquest.expr.ExprNode;
import org.tsers.junitquest.finder.JavaClassFinder;
import org.tsers.junitquest.instance.PrimitiveInstance;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestGenerator {

    private static final int MAX_TRIES_PER_METHOD = 300;
    private static final String STATIC_INITIALIZER = "<clinit>";

    private BranchCoverage bc;

    public String generateTests(String packageName, String outputDirectory) {
        return this.generateTests(packageName, outputDirectory, ClassLoader.getSystemClassLoader());
    }

    public String generateTests(String packageName, String outputDirectory, ClassLoader cl) {

        Instrumenter.instrumentClasses(Arrays.asList(packageName), outputDirectory, cl);

        Class clazz = Instrumenter.getClass(packageName);

        outPutToNull();
        List<MethodNode> methodNodes = Instrumenter.getMethodNodes().stream()
                .filter(m -> !m.name.equals(STATIC_INITIALIZER))
                .collect(Collectors.toList());

        String tests = methodNodes.stream()
                .map(m -> generateTestsForMethod(packageName, m, clazz))
                .reduce("", (a, b) -> a + b);
        outputToStdin();
        return TestCaseGenerator.wrapToTestClass(tests, clazz.getSimpleName());
    }

    private void outputToStdin() {
        PrintStream stdout = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(stdout);
        System.setErr(stdout);
    }

    private void outPutToNull() {
        PrintStream stdNull = new PrintStream(new ByteArrayOutputStream());
        System.setOut(stdNull);
        System.setErr(stdNull);
    }

    public String generateTests(String packageName, String outputDirectory, ClassLoader cl, String runtimeJarLocation) {
        JavaClassFinder.setRuntimeJarLocation(runtimeJarLocation);
        return generateTests(packageName, outputDirectory, cl);
    }

    private String generateTestsForMethod(String packageName, MethodNode methodNode, Class clazz) {
        try {
            List<List<CallParam>> callParams = generateTests(packageName, methodNode.name, methodNode.desc);
            return TestCaseGenerator.toTestCase(callParams, clazz, methodNode.name, methodNode.desc);
        } catch (Exception e) {

        }
        return "";
    }

    public List<List<CallParam>> generateTests(String packagename, String methodName, String methodDesc) throws Exception {

        Class classInTest = Instrumenter.getClass(packagename);
        this.bc = new BranchCoverage(Instrumenter.getMethodNode(packagename, methodName, methodDesc));

        AccessibleObject methodInTest = Jutil.getAccessibleObject(classInTest, methodName, methodDesc);
        List<ExprNode> alreadySolvedExpressions = new ArrayList();
        List<List<CallParam>> usefulMethodCalls = new ArrayList();
        LinkedList<ConcolerInput> concolerInputQueue = new LinkedList<>();
        concolerInputQueue.add(createInitialConcolerInput(methodInTest));

        int escapeCounter = 0;
        while (!bc.isAllVisited() && escapeCounter < MAX_TRIES_PER_METHOD) {
            escapeCounter++;

            if (concolerInputQueue.size() == 0) {
                return usefulMethodCalls;
            }
            ExprNode expression = concolerInputQueue.poll().getExpression();
            if (containsExpression(alreadySolvedExpressions, expression)) {
                continue;
            }
            alreadySolvedExpressions.add(expression);
            List<List<CallParam>> callParamsList = Solver.solveAll(expression);

            for (List<CallParam> callParams : callParamsList) {

                try {

                    List<CallParam> finalCallParams = createFinalCallParams(methodInTest, callParams);

                    Object[] builtCallParameters =
                            TestGenerator.buildCallParams.apply(finalCallParams);

                    ExecutionPath fullPath = Logger.getExecutionPath(methodInTest, builtCallParameters);

                    ExecutionPath methodPath = fullPath.stripBeforeBEGINMETHOD(methodName + " " + methodDesc);
                    ExecutionPath noSubroutines = fullPath.removeSubroutineStates(bc.getTargetLabels());

                    boolean newLabels = bc.visitLabels(methodPath.getLabels());
                    if (newLabels) {
                        usefulMethodCalls.add(finalCallParams);
                    }
                    concolerInputQueue.addAll(InputCreator.createConcolerInputs(methodPath));
                    concolerInputQueue.addAll(InputCreator.createConcolerInputs(noSubroutines));
                } catch (InfiniteLoopException e) {
                    escapeCounter = Integer.MAX_VALUE;
                    break;
                } catch (Exception e) {

                }
            }

        }
        return usefulMethodCalls;
    }

    private List<CallParam> createFinalCallParams(AccessibleObject methodInTest, List<CallParam> callParams) {
        return TestGenerator.createMissingParams
                .andThen(squeeze())
                .andThen(transformPrimitives(methodInTest))
                .apply(callParams)
                .stream().sorted((a, b) ->
                        Integer.compare(a.getPosition(), b.getPosition()))
                .collect(Collectors.toList());
    }

    public BranchCoverage getBc() {
        if (this.bc == null) {
            throw new RuntimeException("Method must be analyzed first");
        }
        return this.bc;
    }


    public static Function<List<CallParam>, Object[]> buildCallParams = callParams -> {
        List<Object> builtCallParams = callParams.stream()
                .map(c -> c.getInstance().build())
                .collect(Collectors.toList());
        return Jutil.listToArray(builtCallParams);

    };
    private static Function<List<CallParam>, List<CallParam>> createMissingParams = callParams -> {
        List<Integer> positions = callParams.stream()
                .map(cp -> cp.getPosition())
                .collect(Collectors.toList());
        List<CallParam> defaultCallarams = InitialExpression.getInitialCallParams();
        List<CallParam> missingCallParams = defaultCallarams.stream()
                .filter(cp -> !positions.contains(cp.getPosition()))
                .collect(Collectors.toList());

        return Jutil.combineLists(callParams, missingCallParams);

    };

    private static Function<List<CallParam>, List<CallParam>> transformPrimitives(AccessibleObject m) {
        return callParams -> callParams.stream().
                map(transformPrimitive(m))
                .collect(Collectors.toList());

    }

    private static Function<CallParam, CallParam> transformPrimitive(AccessibleObject m) {
        return callParam -> {

            int prefix = 0;
            if (!Jutil.isStatic(m)) {
                prefix = 1;
                if (callParam.getPosition() == 0) {
                    return callParam;
                }
            }

            Class<?>[] paramTypes = Jutil.getParameterTypes(m);
            Class clazz = paramTypes[callParam.getPosition() - prefix];

            if (clazz.equals(Boolean.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance(i != 0), callParam.getPosition());
            } else if (clazz.equals(Character.TYPE)) {
                return new CallParam(new PrimitiveInstance('a'), callParam.getPosition());
            } else if (clazz.equals(Integer.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance(i), callParam.getPosition());
            } else if (clazz.equals(Short.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance((short) i), callParam.getPosition());
            } else if (clazz.equals(Byte.TYPE)) {
                byte b = 0;
                return new CallParam(new PrimitiveInstance(b), callParam.getPosition());
            } else if (clazz.equals(Long.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance((long) i), callParam.getPosition());
            } else if (clazz.equals(Float.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance((float) i), callParam.getPosition());
            } else if (clazz.equals(Double.TYPE)) {
                int i = (int) callParam.getInstance().getInstance();
                return new CallParam(new PrimitiveInstance((double) i), callParam.getPosition());
            }
            return callParam;
        };
    }


    //needed to "squeeze" 64 bit primitive types
    private static Function<List<CallParam>, List<CallParam>> squeeze() {
        return callParams -> {
            if (callParams.size() == 0) {
                return callParams;
            }
            int maxLocal = callParams.stream().mapToInt(c -> c.getPosition()).max().getAsInt();
            if (callParams.size() > maxLocal) {
                return callParams;
            }

            List<Integer> positions =
                    callParams.stream()
                            .map(c -> c.getPosition())
                            .collect(Collectors.toList());

            Integer hole =
                    IntStream.range(0, callParams.size())
                            .mapToObj(i -> new Integer(i))
                            .filter(i -> !positions.contains(i))
                            .findFirst().get();

            List<CallParam> notMovedCallParams = callParams.stream()
                    .filter(c -> c.getPosition() <= hole)
                    .collect(Collectors.toList());

            List<CallParam> movedCallParams =
                    callParams.stream()
                            .filter(c -> c.getPosition() > hole)
                            .map(c -> new CallParam(c.getInstance(), c.getPosition() - 1))
                            .collect(Collectors.toList());


            List<CallParam> result = Jutil.combineLists(notMovedCallParams, movedCallParams);

            return squeeze().apply(result);
        };
    }

    private static boolean containsExpression(List<ExprNode> expression, ExprNode tofind) {
        return expression.stream()
                .map(e -> e.equals(tofind))
                .reduce(false, (a, b) -> a || b);
    }

    private static ConcolerInput createInitialConcolerInput(final AccessibleObject method) {
        return new ConcolerInput(null, null) {
            @Override
            public ExprNode getExpression() {
                return InitialExpression.createInitialExpression(method);
            }
        };
    }

}
