package org.tsers.junitquest.expr;


import java.util.List;

public class LessThanEqNode extends ExprNode {

    public LessThanEqNode(List<ExprNode> children) {
        super(children);
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new LessThanNode(newchildren);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }

}
