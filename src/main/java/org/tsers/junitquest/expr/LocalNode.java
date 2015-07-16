package org.tsers.junitquest.expr;

import java.util.List;

public class LocalNode extends ValueNode {
    public LocalNode(int value) {
        super(value);
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new LocalNode(value);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return ((ValueNode) exprNode).getValue() == value;
        }
        return false;
    }

}
