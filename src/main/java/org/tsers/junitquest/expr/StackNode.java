package org.tsers.junitquest.expr;

import java.util.List;

public class StackNode extends ValueNode {
    public StackNode(int value) {
        super(value);
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new StackNode(value);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return ((ValueNode) exprNode).getValue() == value;
        }
        return false;
    }

}
