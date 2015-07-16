package org.tsers.junitquest.solver;

import org.junit.Test;
import org.tsers.junitquest.expr.*;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ArithmeticSolverTest {

    ArithmeticSolver arithmeticSolver = new ArithmeticSolver();

    @Test
    public void solveEquationTest() {

        ExprNode leftSide = new AddNode(Arrays.asList(new IntNode(2), new IntNode(10)));
        ExprNode rightSide = new AddNode(Arrays.asList(new LocalNode(0), new IntNode(100)));

        ExprNode solvedEquation = arithmeticSolver.solveEquation.apply(new EqualNode(Arrays.asList(leftSide, rightSide)), new LocalNode(0));
        assertEquals(0, ((LocalNode) solvedEquation.getChildren().get(0)).getValue());
        assertEquals(-88, ((IntNode) solvedEquation.getChildren().get(1)).getValue());

    }


    @Test
    public void solveEquationTest2() {

        ExprNode leftSide = new AddNode(Arrays.asList(new LocalNode(0), new IntNode(100)));
        ExprNode rightSide = new AddNode(Arrays.asList(new IntNode(2), new IntNode(10)));


        ExprNode solvedEquation = arithmeticSolver.solveEquation.apply(new EqualNode(Arrays.asList(rightSide, leftSide)), new LocalNode(0));
        assertEquals(0, ((LocalNode) solvedEquation.getChildren().get(0)).getValue());
        assertEquals(-88, ((IntNode) solvedEquation.getChildren().get(1)).getValue());

    }

    @Test
    public void solveEquationTest3() {

        ExprNode leftSide = new AddNode(Arrays.asList(new LocalNode(1), new IntNode(100)));
        ExprNode rightSide = new AddNode(Arrays.asList(new IntNode(2), new IntNode(10)));


        ExprNode solvedEquation = arithmeticSolver.solveEquation().apply(new EqualNode(Arrays.asList(rightSide, leftSide)));
        assertEquals(1, ((LocalNode) solvedEquation.getChildren().get(0)).getValue());
        assertEquals(-88, ((IntNode) solvedEquation.getChildren().get(1)).getValue());

    }

    @Test
    public void reduceExpressionTest() {
        ExprNode expr = new AddNode(Arrays.asList(new IntNode(10), new IntNode(20), new MinusNode(new IntNode(-100))));
        ExprNode reduced = ArithmeticSolver.reduceExpression.apply(expr);
        assertEquals(130, ((IntNode) reduced).getValue());
    }

    @Test
    public void reduceExpressionTest2() {
        ExprNode expr = new AddNode(Arrays.asList(
                new AddNode(
                        Arrays.asList(new MinusNode(new IntNode(3)), new MinusNode(new IntNode(3)))),
                new AddNode(
                        Arrays.asList(new MinusNode(new IntNode(3)), new MinusNode(new IntNode(3))))
        ));
        ExprNode reduced = ArithmeticSolver.reduceExpression.apply(expr);
        assertEquals(-12, ((IntNode) reduced).getValue());
    }

    @Test
    public void reduceExpressionTest3() {
        ExprNode expr = new AddNode(Arrays.asList(
                new MinusNode(
                        new AddNode(Arrays.asList(new IntNode(3), new IntNode(3)))
                ),
                new MinusNode(
                        new AddNode(Arrays.asList(new IntNode(4), new IntNode(-40)))
                )
        ));
        ExprNode reduced = ArithmeticSolver.reduceExpression.apply(expr);
        assertEquals(30, ((IntNode) reduced).getValue());
    }

    @Test
    public void reduceExpressionTest4() {
        ExprNode expr = new AddNode(Arrays.asList(new LocalNode(0)));
        ExprNode reduced = ArithmeticSolver.reduceExpression.apply(expr);
        assertEquals(true, reduced.equals(new LocalNode(0)));
    }

    @Test
    public void reduceComplementTest() {
        ExprNode node = ArithmeticSolver.reduceComplement.apply(new MinusNode(new MinusNode(new LocalNode(0))));
        assertEquals(true, new LocalNode(0).equals(node));
    }

    @Test
    public void complementTest() {
        ExprNode node = ArithmeticSolver.complement.apply(new AddNode(Arrays.asList(new IntNode(10), new LocalNode(0))));
        ExprNode expected = new AddNode(Arrays.asList(new IntNode(-10), new MinusNode(new LocalNode(0))));
        assertEquals(true, expected.equals(node));
    }

    @Test
    public void reduceMultiplicationTest() {
        ExprNode initial = new MultiplicationNode(Arrays.asList(new LocalNode(0), new MultiplicationNode(Arrays.asList(new LocalNode(1)))));
        ExprNode result = ArithmeticSolver.reduceMultiplication.apply(initial);
        assertEquals(2, result.getChildren().size());
        assertEquals(true, result.getChildren().stream().anyMatch(c -> c.equals(new LocalNode(0))));
        assertEquals(true, result.getChildren().stream().anyMatch(c -> c.equals(new LocalNode(1))));
    }

    @Test
    public void reduceMultiplicationTest2() {
        ExprNode initial = new MultiplicationNode(
                Arrays.asList(new LocalNode(2), new AddNode(Arrays.asList(new LocalNode(0), new LocalNode(1)))));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(true, result instanceof AddNode);
        assertEquals(2, result.getChildren().size());
        assertEquals(2, result.getChildren().get(0).getChildren().size());
        assertEquals(2, result.getChildren().get(1).getChildren().size());

        assertEquals(true, result.getChildren().get(0).getChildren().stream().anyMatch(c -> c.equals(new LocalNode(0))));
        assertEquals(true, result.getChildren().get(0).getChildren().stream().anyMatch(c -> c.equals(new LocalNode(2))));

        assertEquals(true, result.getChildren().get(1).getChildren().stream().anyMatch(c -> c.equals(new LocalNode(1))));
        assertEquals(true, result.getChildren().get(1).getChildren().stream().anyMatch(c -> c.equals(new LocalNode(1))));

    }


    @Test
    public void reduceMultiplicationTest3() {
        ExprNode initial = new MultiplicationNode(
                Arrays.asList(
                        new AddNode(Arrays.asList(new LocalNode(0), new LocalNode(1))),
                        new AddNode(Arrays.asList(new LocalNode(2), new LocalNode(3)))));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(4, result.getChildren().size());
        assertEquals(AddNode.class, result.getClass());


    }

    @Test
    public void reduceMultiplicationTest4() {
        ExprNode initial = new MultiplicationNode(
                Arrays.asList(
                        new AddNode(Arrays.asList(new IntNode(10), new IntNode(20))),
                        new AddNode(Arrays.asList(new IntNode(30), new IntNode(40)))));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(IntNode.class, result.getClass());
        assertEquals(2100, ((IntNode) result).getValue());


    }

    @Test
    public void reduceMultiplicationTest5() {
        ExprNode initial = new MultiplicationNode(
                Arrays.asList(new MinusNode(new LocalNode(0)), new IntNode(10)));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(2, result.getChildren().size());
        assertEquals(true, result.getChildren().stream().anyMatch(n -> n.equals(new IntNode(-10))));
        assertEquals(true, result.getChildren().stream().anyMatch(n -> n.equals(new LocalNode(0))));

    }

    @Test
    public void reduceMultiplicationTest6() {
        ExprNode initial = new MultiplicationNode(
                Arrays.asList(
                        new MinusNode(new LocalNode(0)), new MinusNode(new LocalNode(1)), new MinusNode(new LocalNode(2))));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(3, result.getChildren().size());
        assertEquals(1, result.getChildren().stream().filter(n -> n instanceof MinusNode).count());
        assertEquals(2, result.getChildren().stream().filter(n -> n instanceof LocalNode).count());
    }

    @Test
    public void reduceMultiplicationTest7() {
        ExprNode initial = new MinusNode(new MultiplicationNode(Arrays.asList(new LocalNode(0), new IntNode(123))));
        ExprNode result = ArithmeticSolver.reduceExpression.apply(initial);

        assertEquals(2, result.getChildren().size());
        assertEquals(true, result instanceof MultiplicationNode);
        assertEquals(true, result.getChildren().stream().anyMatch(n -> n.equals(new LocalNode(0))));
        assertEquals(true, result.getChildren().stream().anyMatch(n -> n.equals(new IntNode(-123))));
    }

}
