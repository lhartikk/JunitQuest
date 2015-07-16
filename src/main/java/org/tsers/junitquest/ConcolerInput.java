package org.tsers.junitquest;


import org.tsers.junitquest.expr.ExprNode;

public class ConcolerInput {
    final ExecutionPath executionPath;
    final ExprNode node;
    private final Concoler concoler = new Concoler();

    public ConcolerInput(ExecutionPath executionPath, ExprNode condition) {
        this.executionPath = executionPath;
        this.node = condition;
    }


    public ExprNode getExpression() {
        try {
            ExprNode expr = concoler.concole(executionPath, node);
            return expr;
        } catch (Exception e) {
            return null;
        }

    }

}