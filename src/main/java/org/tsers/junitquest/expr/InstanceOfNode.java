package org.tsers.junitquest.expr;

import java.util.Arrays;
import java.util.List;

public class InstanceOfNode extends ExprNode {

    final String type;

    public InstanceOfNode(ExprNode child, String instance) {
        super(Arrays.asList(child));
        this.type = instance;
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        if (newchildren.size() != 1) {
            throw new RuntimeException("INSTANCEOF CAN HAVE ONLY 1 CHILD!");
        }
        return new InstanceOfNode(newchildren.get(0), this.type);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            boolean typeEqs = ((InstanceOfNode) exprNode).getType().equals(type);
            return childrenEquals(exprNode.getChildren()) && typeEqs;
        }
        return false;
    }

    public String getType() {
        return type;
    }

}
