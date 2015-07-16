package org.tsers.junitquest.expr;

import java.util.Arrays;
import java.util.List;

public class MinusNode extends ExprNode {


    public MinusNode(ExprNode child) {
        super(Arrays.asList(child));
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        if (newchildren.size() != 1) {
            throw new RuntimeException("MINUS NODE CAN HAVE ONLY ONE CHILDREN");
        }
        return new MinusNode(newchildren.get(0));
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }

}
