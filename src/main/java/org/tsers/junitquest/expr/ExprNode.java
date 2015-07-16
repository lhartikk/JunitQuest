package org.tsers.junitquest.expr;


import java.util.List;

public abstract class ExprNode {

    private final List<ExprNode> children;

    public ExprNode(List<ExprNode> children) {
        this.children = children;
    }

    public List<ExprNode> getChildren() {
        return children;
    }

    abstract public ExprNode copy(List<ExprNode> newchildren);

    abstract public boolean equals(ExprNode exprNode);

    public boolean childrenEquals(List<ExprNode> otherChildren) {
        if (otherChildren.size() != children.size()) {
            return false;
        }
        for (int i = 0; i < children.size(); i++) {
            ExprNode a = children.get(i);
            ExprNode b = otherChildren.get(i);
            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }

}
