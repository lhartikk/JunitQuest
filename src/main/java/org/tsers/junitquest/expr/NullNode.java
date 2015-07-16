package org.tsers.junitquest.expr;

import java.util.List;

public class NullNode extends ConstantNode {

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new NullNode();
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        return exprNode instanceof NullNode;
    }

}
