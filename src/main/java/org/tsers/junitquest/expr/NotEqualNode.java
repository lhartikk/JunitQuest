package org.tsers.junitquest.expr;


import java.util.List;

public class NotEqualNode extends ExprNode {

    public NotEqualNode(List<ExprNode> children) {
        super(children);
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new NotEqualNode(newchildren);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }

}
