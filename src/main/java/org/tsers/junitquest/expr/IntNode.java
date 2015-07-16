package org.tsers.junitquest.expr;

import java.util.List;

public class IntNode extends ConstantNode {

    final int value;

    public IntNode(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new IntNode(value);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return ((IntNode) exprNode).getValue() == value;
        }
        return false;
    }

}
