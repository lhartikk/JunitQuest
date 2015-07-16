package org.tsers.junitquest.expr;

import java.util.List;
import java.util.Optional;

public class InvokeVirtualNode extends InvokeMethodNode {

    public InvokeVirtualNode(List<ExprNode> children, String className, String methodName, String methodDesc, Optional<Object> returnValue) {
        super(children, className, methodName, methodDesc, returnValue);
    }
}
