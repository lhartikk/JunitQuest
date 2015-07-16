package org.tsers.junitquest.expr;


import java.util.ArrayList;

public abstract class ValueNode extends ExprNode {

    protected final int value;

    public ValueNode(int value) {
        super(new ArrayList<>());
        this.value = value;

    }

    public int getValue() {
        return value;
    }

}
