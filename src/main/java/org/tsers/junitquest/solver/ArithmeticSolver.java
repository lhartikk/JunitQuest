package org.tsers.junitquest.solver;

import org.tsers.junitquest.CallParam;
import org.tsers.junitquest.Jutil;
import org.tsers.junitquest.instance.PrimitiveInstance;
import org.tsers.junitquest.expr.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArithmeticSolver {

    private static final int MAX_REDUCTION_TRYS = 5;

    public static boolean isArithmeticEquation(ExprNode exprNode) {
        return Jutil.containsClazz(exprNode, AddNode.class) ||
                Jutil.containsClazz(exprNode, MinusNode.class) ||
                Jutil.containsClazz(exprNode, GreaterThanEqNode.class) ||
                Jutil.containsClazz(exprNode, GreaterThanNode.class) ||
                Jutil.containsClazz(exprNode, LessThanNode.class) ||
                Jutil.containsClazz(exprNode, IntNode.class)
                        && !InstanceOfSolver.isInstanceOfEquation(exprNode)

                ;
    }

    public static Function<ExprNode, ExprNode> complement = node -> {
        if (node instanceof IntNode) {
            int v = ((IntNode) node).getValue();
            return new IntNode(-v);
        } else if (node instanceof MinusNode) {
            return node.getChildren().get(0);
        } else if (node instanceof AddNode) {
            return Jutil.mapChildren.apply(node, ArithmeticSolver.complement);
        } else {
            return new MinusNode(node);
        }

    };

    public static Function<ExprNode, ExprNode> reduceComplement = minusNode ->
    {
        if (!(minusNode instanceof MinusNode)) {
            return Jutil.mapChildren.apply(minusNode, ArithmeticSolver.reduceComplement);
        }

        ExprNode child = minusNode.getChildren().get(0);
        if (child instanceof MinusNode) {
            return ArithmeticSolver.reduceComplement.apply(child.getChildren().get(0));
        } else if (child instanceof IntNode) {
            return new IntNode(-((IntNode) child).getValue());
        } else if (child instanceof AddNode) {
            return Jutil.mapChildren.apply(child, ArithmeticSolver.complement);
        } else if (child instanceof MultiplicationNode) {
            return Jutil.mapFirstChild.apply(child, ArithmeticSolver.complement);
        } else {
            return minusNode;
        }

    };
    public static BiFunction<ExprNode, Integer, ExprNode> multiply = (node, multiplier) -> {
        if (node instanceof DivisionNode) {
            if (node.getChildren().stream()
                    .filter(n -> n instanceof IntNode && ((IntNode) n).getValue() == multiplier)
                    .findAny().isPresent()) {
                ExprNode variable = node.getChildren().stream()
                        .filter(n -> !(n instanceof IntNode && ((IntNode) n).getValue() == multiplier))
                        .findAny().get();
                return variable;
            }
            return new MultiplicationNode(Arrays.asList(node, new IntNode(multiplier)));
        } else if (node instanceof IntNode) {
            return new IntNode(((IntNode) node).getValue() * multiplier);
        } else {
            return new MultiplicationNode(Arrays.asList(node, new IntNode(multiplier)));
        }

    };
    public static BiFunction<ExprNode, Integer, ExprNode> divide = (node, denominator) -> {
        if (node instanceof MultiplicationNode) {
            if (node.getChildren().stream()
                    .filter(n -> n instanceof IntNode && ((IntNode) n).getValue() == denominator)
                    .findAny().isPresent()) {
                ExprNode variable = node.getChildren().stream()
                        .filter(n -> !(n instanceof IntNode && ((IntNode) n).getValue() == denominator))
                        .findAny().get();
                return variable;
            }
            return new DivisionNode(Arrays.asList(node, new IntNode(denominator)));
        } else if (node instanceof IntNode) {
            if (denominator == 0) {
                return new IntNode(new Random().nextInt());
            } else {
                return new IntNode(((IntNode) node).getValue() / denominator);
            }
        } else {
            return new DivisionNode(Arrays.asList(node, new IntNode(denominator)));
        }

    };
    private static Function<ExprNode, ExprNode> setAddNodeAsParent = node ->
    {
        if (!(node instanceof AddNode)) {
            return new AddNode(Arrays.asList(node));
        }
        return node;
    };
    private static Function<ExprNode, ExprNode> addConstantsTogether = addNode -> {
        if (!(addNode instanceof AddNode)) {
            return addNode;
        }
        List<ExprNode> sum = addNode.getChildren().stream()
                .filter(n -> n instanceof IntNode)
                .map(n -> ((IntNode) n).getValue())
                .reduce((a, b) -> a + b)
                .map(v -> Arrays.<ExprNode>asList(new IntNode(v)))
                .orElse(Arrays.<ExprNode>asList());

        List<ExprNode> variables = addNode.getChildren().stream()
                .filter(n -> (!(n instanceof IntNode)))
                .collect(Collectors.toList());

        List<ExprNode> addedFactors = Jutil.combineLists(sum, variables);
        return new AddNode(addedFactors);
    };

    public static Function<ExprNode, LocalNode> getSmallestLocationNode() {
        return equation -> {
            LocalNode smallesLocationNode =
                    (LocalNode) Jutil.findAllNodeTypes(LocalNode.class).apply(equation)
                            .stream()
                            .sorted((a, b) ->
                                    Integer.compare(((LocalNode) a).getValue(), ((LocalNode) b).getValue()))
                            .findFirst().get();
            return smallesLocationNode;
        };
    }

    public static Function<ExprNode, ExprNode> solveEquation() {
        return equation -> {
            ExprNode solveInrespective = getSmallestLocationNode().apply(equation);
            return ArithmeticSolver.
                    solveEquation.apply(equation, solveInrespective);
        };
    }

    private static boolean equalsOrComplementEquals(ExprNode node, ExprNode nodeToFind) {

        if (node.equals(nodeToFind) || complement.apply(node).equals(nodeToFind)) {
            return true;
        }

        if (node.getChildren().size() == 0) {
            return false;
        }

        return node.getChildren().stream()
                .map(n -> equalsOrComplementEquals(n, nodeToFind))
                .reduce(false, (a, b) -> a || b);
    }

    /*
    takes an expression that has only LocalNodes of same type
    return the function that when applied reduces to one Localnode only
     */
    public static Function<ExprNode, ExprNode> getFunctionsToReduceValues(ExprNode values) {

        if (values instanceof MinusNode) {
            return complement;
        } else if (values instanceof LocalNode) {
            return node -> node;
        } else if (values instanceof MultiplicationNode) {
            int i = ((IntNode) values.getChildren().stream()
                    .filter(n -> n instanceof IntNode)
                    .findFirst().get()).getValue();
            return node -> divide.apply(node, i);
        } else if (values instanceof DivisionNode) {
            int i = ((IntNode) values.getChildren().stream()
                    .filter(n -> n instanceof IntNode)
                    .findFirst().get()).getValue();
            return node -> multiply.apply(node, i);
        } else {
            return node -> node;
        }


    }

    private static Function<ExprNode, ExprNode> reduceMinusInMultiplication
            = multiplicationNode -> {

        List<ExprNode> minusNodes =
                multiplicationNode.getChildren().stream()
                        .filter(n -> n instanceof MinusNode)
                        .collect(Collectors.toList());
        List<ExprNode> nonMinusNodes =
                multiplicationNode.getChildren().stream()
                        .filter(n -> !(n instanceof MinusNode))
                        .collect(Collectors.toList());

        if (minusNodes.size() > 1) {
            if (minusNodes.size() % 2 == 0) {
                List<ExprNode> complemented = minusNodes.stream()
                        .map(n -> complement.apply(n))
                        .collect(Collectors.toList());
                return new MultiplicationNode(Jutil.combineLists(nonMinusNodes, complemented));
            } else {
                List<ExprNode> complementedButFirst = minusNodes.stream()
                        .skip(1)
                        .map(n -> complement.apply(n))
                        .collect(Collectors.toList());

                return new MultiplicationNode(
                        Jutil.combineLists(Arrays.asList(minusNodes.get(0)), nonMinusNodes, complementedButFirst));
            }
        }
        if (minusNodes.size() == 1) {
            if (nonMinusNodes.stream().anyMatch(n -> n instanceof IntNode)) {
                if (nonMinusNodes.stream().filter(n -> n instanceof IntNode).count() != 1) {
                    return multiplicationNode;
                }

                ExprNode complementedIntNode = nonMinusNodes.stream()
                        .filter(n -> n instanceof IntNode)
                        .map(complement)
                        .findFirst().get();

                List<ExprNode> rest = nonMinusNodes.stream()
                        .filter(n -> !(n instanceof IntNode))
                        .collect(Collectors.toList());

                return new MultiplicationNode(
                        Jutil.combineLists(
                                Arrays.asList(complementedIntNode), rest,
                                Arrays.asList(complement.apply(minusNodes.get(0))))
                );
            }
        }
        return multiplicationNode;
    };
    private static Function<ExprNode, ExprNode> multiplyIntegersTogether
            = multiplicationNode -> {

        List<ExprNode> intNodes =
                multiplicationNode.getChildren().stream()
                        .filter(n -> n instanceof IntNode)
                        .collect(Collectors.toList());

        if (intNodes.size() > 1) {
            int multiplicationResult =
                    intNodes.stream()
                            .map(n -> ((IntNode) n).getValue())
                            .reduce(1, (a, b) -> a * b);
            ExprNode resultNode = new IntNode(multiplicationResult);
            List<ExprNode> rest = multiplicationNode.getChildren().stream()
                    .filter(n -> !(n instanceof IntNode))
                    .collect(Collectors.toList());

            if (rest.size() == 0) {
                return resultNode;
            }
            return new MultiplicationNode(
                    Jutil.combineLists(rest, Arrays.asList(resultNode)));
        }

        return multiplicationNode;
    };
    private static BiFunction<ExprNode, Optional<ExprNode>, ExprNode>
            transformMultiplications = (multiplicationNode, addNode) -> {
        if (!addNode.isPresent()) {
            return multiplicationNode;
        }
        List<ExprNode> rest =
                multiplicationNode.getChildren().stream()
                        .filter(n -> !n.equals(addNode.get()))
                        .collect(Collectors.toList());

        List<ExprNode> factors =
                addNode.get().getChildren().stream()
                        .map(n ->
                                new MultiplicationNode(
                                        Jutil.combineLists(Arrays.asList(n), rest)))
                        .collect(Collectors.toList());
        return new AddNode(factors);
    };
    public static Function<ExprNode, ExprNode> reduceMultiplication = multiplicationNode -> {
        if (!(multiplicationNode instanceof MultiplicationNode)) {
            return Jutil.mapChildren.apply(multiplicationNode, ArithmeticSolver.reduceMultiplication);
        }

        Optional<ExprNode> addNode =
                multiplicationNode.getChildren().stream()
                        .filter(n -> n instanceof AddNode)
                        .findFirst();
        ExprNode transformed =
                ArithmeticSolver.transformMultiplications
                        .andThen(ArithmeticSolver.multiplyIntegersTogether)
                        .andThen(ArithmeticSolver.reduceMinusInMultiplication)
                        .apply(multiplicationNode, addNode);

        return LogicalSolver.combineNodes(transformed, MultiplicationNode.class);
    };

    public static Function<ExprNode, ExprNode> reduceExpression = node -> {
        ExprNode reducted =
                ArithmeticSolver.addConstantsTogether
                        .andThen(ArithmeticSolver.reduceMultiplication)
                        .andThen(LogicalSolver.reduceAdds)
                        .andThen(ArithmeticSolver.reduceComplement)
                        .apply(node);
        if (!reducted.equals(node)) {
            return ArithmeticSolver.reduceExpression.apply(reducted);
        }

        return node;
    };

    public static BiFunction<ExprNode, ExprNode, ExprNode> solveEquation = (equation, inRespective) -> {
        {
            ExprNode leftSide = ArithmeticSolver.reduceExpression
                    .andThen(setAddNodeAsParent)
                    .apply(equation.getChildren().get(0));
            List<ExprNode> leftSideValues = leftSide.getChildren().stream()
                    .filter(n -> equalsOrComplementEquals(n, inRespective))
                    .collect(Collectors.toList());

            List<ExprNode> leftSideConstants = leftSide.getChildren().stream()
                    .filter(n -> !equalsOrComplementEquals(n, inRespective))
                    .map(ArithmeticSolver.complement)
                    .collect(Collectors.toList());

            ExprNode rightSide = ArithmeticSolver.reduceExpression
                    .andThen(setAddNodeAsParent)
                    .apply(equation.getChildren().get(1));

            List<ExprNode> rightSideValues = rightSide.getChildren().stream()
                    .filter(n -> equalsOrComplementEquals(n, inRespective))
                    .map(ArithmeticSolver.complement)
                    .collect(Collectors.toList());

            List<ExprNode> rightSideConstants = rightSide.getChildren().stream()
                    .filter(n -> !equalsOrComplementEquals(n, inRespective))
                    .collect(Collectors.toList());

            ExprNode constants =
                    ArithmeticSolver.reduceExpression
                            .apply(
                                    new AddNode(
                                            Jutil.combineLists(leftSideConstants, rightSideConstants)
                                    ));

            ExprNode values =
                    ArithmeticSolver.reduceExpression
                            .apply(
                                    new AddNode(
                                            Jutil.combineLists(leftSideValues, rightSideValues)
                                    ));


            return reduceValuesAndConstants(equation, constants, values);
        }
    };

    private static ExprNode reduceValuesAndConstants(ExprNode equation, ExprNode constants, ExprNode values) {
        //the equation may be "not solvable", so let's limit the trys
        for (int i = 0; i < MAX_REDUCTION_TRYS; i++) {
            Function<ExprNode, ExprNode> reduceFunctions = getFunctionsToReduceValues(values);
            values = reduceFunctions.apply(values);
            constants = reduceFunctions.apply(constants);

            if ((values instanceof LocalNode || values instanceof InvokeVirtualNode ||
                    values instanceof GetFieldNode)) {
                return Jutil.createNode.apply(equation.getClass(), Arrays.asList(values, constants));
            }
        }
        throw new RuntimeException("Cannot reduce expression");
    }


    public static Function<ExprNode, CallParam> solvedEquationToCallParam = equation ->
    {
        Integer location = ((LocalNode) equation.getChildren().get(0)).getValue();
        Integer solvedValueFromEquation = ((IntNode) equation.getChildren().get(1)).getValue();
        int callParamValue = solvedValueFromEquation + getValueToSatisfyEquation(equation);
        return new CallParam(new PrimitiveInstance(callParamValue), location);
    };

    private static int getValueToSatisfyEquation(ExprNode equationRoot) {
        if (equationRoot instanceof EqualNode) {
            return 0;
        } else if (equationRoot instanceof NotEqualNode) {
            return (new Random()).nextInt();
        } else if (equationRoot instanceof LessThanNode) {
            return -1;
        } else if (equationRoot instanceof LessThanEqNode) {
            return -1;
        } else if (equationRoot instanceof GreaterThanNode) {
            return 1;
        } else if (equationRoot instanceof GreaterThanEqNode) {
            return 1;
        }
        return 0;
    }

}
