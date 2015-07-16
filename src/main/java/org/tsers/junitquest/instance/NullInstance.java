package org.tsers.junitquest.instance;

public class NullInstance implements Instance {

    @Override
    public Object build() {
        return null;
    }

    @Override
    public Object getInstance() {
        return null;
    }

    @Override
    public String asString() {
        return "null";
    }
}
