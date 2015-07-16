package org.tsers.junitquest.solver;

import org.tsers.junitquest.CallParam;
import org.tsers.junitquest.InstanceHelper;
import org.tsers.junitquest.Jutil;
import org.tsers.junitquest.expr.ExprNode;
import org.tsers.junitquest.expr.InstanceOfNode;
import org.tsers.junitquest.expr.LocalNode;
import org.tsers.junitquest.instance.ClassInstance;
import org.tsers.junitquest.instance.Instance;
import org.tsers.junitquest.instance.ObjectInstance;
import org.tsers.junitquest.instance.PrimitiveInstance;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InstanceOfSolver {

    private static int MAX_INSTANCE_FINDING_RECURSION = 3;

    public static boolean isInstanceOfEquation(ExprNode exprNode) {
        return Jutil.containsClazz(exprNode, InstanceOfNode.class);
    }

    public static Function<ExprNode, List<CallParam>> solveInstanceOfEquation() {
        return node -> {
            InstanceOfNode instanceOfNode = (InstanceOfNode) node.getChildren().stream()
                    .filter(n -> n instanceof InstanceOfNode)
                    .findFirst().get();
            LocalNode localNode = (LocalNode) instanceOfNode.getChildren().get(0);
            String instance = instanceOfNode.getType();
            List<Class> implementingClasses = InstanceHelper.get().getAllImplementingClasses(instance);
            return Arrays.asList(
                    implementingClasses.stream()
                            .map(c -> new CallParam(createInstance(c), localNode.getValue()))
                            .filter(cp -> cp.getInstance() != null)
                            .findAny().get());
        };
    }

    public static Instance createInstance(Class clazz) {
        return createInstanceRecursion(clazz, MAX_INSTANCE_FINDING_RECURSION);
    }

    private static boolean containsClassInConstructorParams(Constructor c, Class clazz) {

        return Arrays.asList(c.getParameterTypes()).contains(clazz);
    }

    private static Instance createArrayInstance(Class clazz) {
        try {
            Method m = Array.class.getMethod("newInstance", Class.class, Integer.TYPE);
            return new ObjectInstance(m, null, Arrays.asList(new ClassInstance(clazz), new PrimitiveInstance(1)));

        } catch (Exception e) {
            throw new RuntimeException("Cannot create array");
        }
    }


    private static Instance createPrimitiveInstance(Class clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return new PrimitiveInstance(true);
        } else if (clazz.equals(Character.TYPE)) {
            return new PrimitiveInstance('a');
        } else if (clazz.equals(Integer.TYPE)) {
            return new PrimitiveInstance(0);
        } else if (clazz.equals(Short.TYPE)) {
            return new PrimitiveInstance((short) 0);
        } else if (clazz.equals(Byte.TYPE)) {
            byte b = 0;
            return new PrimitiveInstance(b);
        } else if (clazz.equals(Long.TYPE)) {
            return new PrimitiveInstance((long) 0);
        } else if (clazz.equals(Float.TYPE)) {
            return new PrimitiveInstance((float) 0);
        } else if (clazz.equals(Double.TYPE)) {
            return new PrimitiveInstance((double) 0);
        }
        throw new RuntimeException("Cannot create primitive instance");
    }

    public static Instance createInstanceRecursion(Class clazz, final int recursionLevel) {

        if (recursionLevel == 0) {
            return null;
        } else if (Jutil.isClassArray(clazz)) {
            Instance i = createArrayInstance(clazz.getComponentType());
            return i;
        } else if (Jutil.isPrimitive(clazz)) {
            return createPrimitiveInstance(clazz);
        }

        Instance instance1 = createFromConstructors(clazz, recursionLevel);
        if (instance1 != null) {
            return instance1;
        }
        return createFromMethodReturningClass(clazz, recursionLevel);
    }

    private static Instance createFromMethodReturningClass(Class clazz, final int recursionLevel) {
        List<Method> methodsReturningClass = getStaticMethodsReturningClass(clazz);
        for (Method method : methodsReturningClass) {
            try {
                List<Instance> parameters = Jutil.arrayToList(method.getParameterTypes()).stream()
                        .map(c -> createInstanceRecursion(c, recursionLevel - 1))
                        .collect(Collectors.toList());

                ObjectInstance instance = new ObjectInstance(method, null, parameters);
                instance.build();
                return instance;
            } catch (Exception ex) {
                continue;
            }
        }
        return null;
    }

    private static Instance createFromConstructors(Class clazz, final int recursionLevel) {
        List<Constructor> constructors = getConstructors(clazz);
        if (Modifier.isAbstract(clazz.getModifiers())) {
            constructors = InstanceHelper.get().getAllImplementingClasses(clazz.getName()).stream()
                    .map(c -> getConstructors(c))
                    .flatMap(c -> c.stream())
                    .filter(c -> !containsClassInConstructorParams(c, clazz))
                    .collect(Collectors.toList());

        }

        for (Constructor constructor : constructors) {
            try {
                List<Instance> parameters = Jutil.arrayToList(constructor.getParameterTypes()).stream()
                        .map(c -> createInstanceRecursion(c, recursionLevel - 1))
                        .collect(Collectors.toList());

                ObjectInstance instance = new ObjectInstance(constructor, parameters);
                instance.build();
                return instance;
            } catch (Exception ex) {
                continue;
            }
        }
        return null;
    }

    private static List<Constructor> getConstructors(Class clazz) {
        return Arrays.asList(clazz.getConstructors());
    }


    private static List<Method> getStaticMethodsReturningClass(Class clazz) {
        return Arrays.asList(clazz.getMethods()).stream()
                .filter(m -> m.getReturnType().isAssignableFrom(clazz))
                .filter(m -> Jutil.isStatic(m))
                .collect(Collectors.toList());


    }

}
