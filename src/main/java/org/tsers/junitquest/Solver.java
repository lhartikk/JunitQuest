package org.tsers.junitquest;

import com.google.common.collect.Sets;
import org.tsers.junitquest.expr.*;
import org.tsers.junitquest.solver.ArithmeticSolver;
import org.tsers.junitquest.solver.NullEquationSolver;
import org.tsers.junitquest.solver.InstanceOfSolver;
import org.tsers.junitquest.solver.LogicalSolver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Solver {

    public static List<List<CallParam>> solveAll(ExprNode node) {
        List<List<CallParam>> cp1 = solve(node);
        ExprNode exp2 = Jutil.applyRecursively(transformMethodNode).
                andThen(Jutil.applyRecursively(transformFieldNode)).apply(node);

        List<List<CallParam>> cp2 = solve(exp2);

        return Jutil.combineLists(cp1, cp2);
    }

    public static List<List<CallParam>> solve(ExprNode node) {
        try {
            List<ExprNode> initialEquations = getEquations(node);

            List<CallParam> solvedArithmeticEquations = initialEquations.stream()
                    .filter(n -> ArithmeticSolver.isArithmeticEquation(n))
                    .map(e -> replaceExtraVariablesWithConstants(e))
                    .flatMap(e -> e.stream())
                    .map(ArithmeticSolver.solveEquation())
                    .map(ArithmeticSolver.solvedEquationToCallParam)
                    .collect(Collectors.toList());

            List<CallParam> solvedInstanceOfEquations = initialEquations.stream()
                    .filter(n -> InstanceOfSolver.isInstanceOfEquation(n))
                    .map(InstanceOfSolver.solveInstanceOfEquation())
                    .flatMap(s -> s.stream())
                    .collect(Collectors.toList());

            List<CallParam> solvedNullEquations = initialEquations.stream()
                    .filter(e -> NullEquationSolver.isNullEquation(e))
                    .map(NullEquationSolver.nullEquationToCallParam)
                    .collect(Collectors.toList());


            List<CallParam> allCallParams
                    = Jutil.combineLists(solvedArithmeticEquations, solvedInstanceOfEquations, solvedNullEquations);

            List<Set<CallParam>> grouped =
                    allCallParams.stream().collect(Collectors.groupingBy(c -> c.getPosition()))
                            .entrySet().stream()
                            .map(e -> e.getValue())
                            .map(c -> new HashSet<CallParam>(c))
                            .collect(Collectors.toList());

            List<List<CallParam>> products = new ArrayList<>(Sets.cartesianProduct(grouped));
            return products;
        } catch (Exception e) {
            return Arrays.asList();
        }
    }

    private static List<ExprNode> getEquations(ExprNode node) {
        return LogicalSolver.combineANDs
                .apply(new AndNode(Arrays.asList(node))).getChildren();
    }


    public static Function<ExprNode, ExprNode> transformMethodNode = node -> {
        if (node instanceof InvokeMethodNode) {
            if (((InvokeMethodNode) node).getReturnValue().isPresent()) {
                Object c = ((InvokeMethodNode) node).getReturnValue().get();
                if (c instanceof Integer) {
                    return new IntNode(((Integer) c).intValue());
                }
            }
        }
        return node;
    };

    public static Function<ExprNode, ExprNode> transformFieldNode = node -> {
        if (node instanceof GetFieldNode) {
            if (((GetFieldNode) node).getReturnValue().isPresent()) {
                Object c = ((GetFieldNode) node).getReturnValue().get();
                if (c instanceof Integer) {
                    return new IntNode(((Integer) c).intValue());
                }
            }
        }
        return node;
    };

    public static List<ExprNode> replaceExtraVariablesWithConstants(ExprNode node) {

        Map<Integer, List<ExprNode>> groupedByLocals =
                Jutil.findAllNodeTypes(LocalNode.class).apply(node)
                        .stream()
                        .collect(Collectors.groupingBy(n -> ((LocalNode) n).getValue()));

        if (groupedByLocals.keySet().size() > 1) {
            int random = (new Random()).nextInt();

            List<Integer> replacedInts =
                    groupedByLocals.keySet().stream().skip(1).collect(Collectors.toList());

            List<ExprNode> eqs = createOriginalReplaceableEquations(random, replacedInts);
            List<ExprNode> replaced = createReplaceEquations(node, random, replacedInts);

            //replace max 10 times
            for (int limit = 0; limit < 10; limit++) {
                if (!hasMultipleVariables(replaced)) {
                    break;
                }
                replaced = replaced.stream()
                        .map(e -> {
                            ExprNode r = e;
                            for (Integer i : replacedInts) {
                                r = Jutil.applyRecursively(Solver.localNodeToIntNode(i, random)).apply(r);
                            }
                            return r;
                        })
                        .collect(Collectors.toList());
            }
            return Jutil.combineLists(replaced, eqs);
        } else {
            return Arrays.asList(node);
        }
    }

    private static List<ExprNode> createReplaceEquations(ExprNode node, int random, List<Integer> replacedInts) {
        return replacedInts.stream()
                .map(i -> Arrays.asList(
                        Jutil.applyRecursively(localNodeToIntNode(i, 0)).apply(node),
                        Jutil.applyRecursively(localNodeToIntNode(i, 1)).apply(node),
                        Jutil.applyRecursively(localNodeToIntNode(i, random)).apply(node)
                ))
                .flatMap(e -> e.stream())
                .collect(Collectors.toList());
    }

    private static List<ExprNode> createOriginalReplaceableEquations(int random, List<Integer> replacedInts) {
        return replacedInts.stream().map(i ->
                Arrays.asList(
                        new EqualNode(Arrays.asList(new LocalNode(i), new IntNode(random))),
                        new EqualNode(Arrays.asList(new LocalNode(i), new IntNode(0))),
                        new EqualNode(Arrays.asList(new LocalNode(i), new IntNode(1)))
                ))
                .flatMap(e -> e.stream())
                .collect(Collectors.toList());
    }

    private static boolean hasMultipleVariables(List<ExprNode> node) {
        return node.stream()
                .map(e -> hasMultipleVariables(e))
                .reduce(false, (a, b) -> a || b);

    }

    private static boolean hasMultipleVariables(ExprNode node) {
        Map<Integer, List<ExprNode>> o =
                Jutil.findAllNodeTypes(LocalNode.class).apply(node)
                        .stream()
                        .collect(Collectors.groupingBy(n -> ((LocalNode) n).getValue()));

        return o.keySet().size() > 1;
    }

    private static Function<ExprNode, ExprNode> localNodeToIntNode
            (Integer localValue, Integer intValue) {
        return node -> {
            if (node instanceof LocalNode) {
                if (((LocalNode) node).getValue() == localValue) {
                    return new IntNode(intValue);
                }
            }
            return node;
        };
    }

}
