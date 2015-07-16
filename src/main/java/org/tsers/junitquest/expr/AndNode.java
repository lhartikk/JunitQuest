package org.tsers.junitquest.expr;

import java.util.List;

public class AndNode extends ExprNode {

    public AndNode(List<ExprNode> children) {
        super(children);
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new AndNode(newchildren);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }

}
