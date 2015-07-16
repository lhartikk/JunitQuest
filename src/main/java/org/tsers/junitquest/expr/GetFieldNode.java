package org.tsers.junitquest.expr;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GetFieldNode extends ExprNode {

    private final String className;
    private final String fieldName;

    private final Optional<Object> returnValue;

    public GetFieldNode(ExprNode child, String className, String fieldName, Optional<Object> returnValue) {
        super(Arrays.asList(child));
        this.className = className;
        this.fieldName = fieldName;
        this.returnValue = returnValue;

    }

    public Optional<Object> getReturnValue() {
        return this.returnValue;
    }

    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new GetFieldNode(newchildren.get(0), className, fieldName, returnValue);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            GetFieldNode gfn = (GetFieldNode) exprNode;
            if (!(gfn.className.equals(className) && gfn.fieldName.equals(fieldName))) {
                return false;
            }
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }

}
