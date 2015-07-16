package org.tsers.junitquest;


import org.tsers.junitquest.instance.Instance;

public class CallParam {


    private final Instance instance;
    private final int position;

    public CallParam(Instance instance, int position) {
        this.instance = instance;
        this.position = position;
    }


    public int getPosition() {
        return position;
    }

    public Instance getInstance() {
        return instance;
    }

}
