package org.tsers.junitquest.expr;

import java.util.List;
import java.util.Optional;

public abstract class InvokeMethodNode extends ExprNode {

    private final String className;
    private final String methodName;
    private final String methodDesc;

    private final Optional<Object> returnValue;

    public InvokeMethodNode(List<ExprNode> children, String className,
                            String methodName, String methodDesc, Optional<Object> returnValue) {
        super(children);
        this.className = className;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.returnValue = returnValue;

    }

    public Optional<Object> getReturnValue() {
        return returnValue;
    }


    @Override
    public ExprNode copy(List<ExprNode> newchildren) {
        return new InvokeVirtualNode(newchildren, className, methodName, methodDesc, returnValue);
    }

    @Override
    public boolean equals(ExprNode exprNode) {
        if (exprNode.getClass().equals(this.getClass())) {
            InvokeMethodNode iv = (InvokeMethodNode) exprNode;
            if (!(iv.className.equals(className) && iv.methodName.equals(methodName) && iv.methodDesc.equals(methodDesc))) {
                return false;
            }
            return childrenEquals(exprNode.getChildren());
        }
        return false;
    }
}
