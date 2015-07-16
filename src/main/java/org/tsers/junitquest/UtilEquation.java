package org.tsers.junitquest;

import org.tsers.junitquest.expr.*;

import java.util.Arrays;

public class UtilEquation {

    public static ExprNode stackTopConditionZero(Class condition) {
        ExprNode stack0 = new StackNode(0);
        ExprNode local = new IntNode(0);
        return Jutil.createNode.apply(condition, Arrays.asList(stack0, local));
    }

    public static ExprNode stackTopEqualsOne() {
        ExprNode stack0 = new StackNode(0);
        ExprNode local = new IntNode(1);
        return new EqualNode(Arrays.asList(stack0, local));
    }

    public static ExprNode stackTopEqualsMinusOne() {
        ExprNode stack0 = new StackNode(0);
        ExprNode local = new IntNode(-1);
        return new EqualNode(Arrays.asList(stack0, local));
    }


    public static ExprNode stackTopsCondition(Class condition) {
        ExprNode stack0 = new StackNode(0);
        ExprNode stack1 = new StackNode(1);
        return Jutil.createNode.apply(condition, Arrays.asList(stack0, stack1));
    }

    public static ExprNode stackTopNull() {
        ExprNode stackNode = new StackNode(0);
        ExprNode objectNode = new NullNode();
        return new EqualNode(Arrays.asList(stackNode, objectNode));
    }


}
