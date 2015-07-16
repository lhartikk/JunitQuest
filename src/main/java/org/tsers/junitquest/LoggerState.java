package org.tsers.junitquest;

import java.util.Optional;


public class LoggerState {

    private final int opcode;
    private int identifier;
    private final Object operand;
    //the value this state returns, given the state is a method call
    Optional<Object> returnValue;

    public LoggerState(int opcode, Object operand, int identifier) {
        this.opcode = opcode;
        this.operand = operand;
        this.returnValue = Optional.empty();
        this.identifier = identifier;
    }

    public LoggerState(int opcode, Object operand) {
        this.opcode = opcode;
        this.operand = operand;
        this.returnValue = Optional.empty();
        this.identifier = -1;
    }

    public Optional<Object> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = Optional.of(returnValue);
    }

    public String stateAsString() {
        return OpcodeHelper.getName(this.opcode) + " " + (this.operand == null ? "" : this.operand);
    }


    public int getOpcode() {
        return this.opcode;
    }

    public Object getOperand() {
        return this.operand;
    }

    public int getIdentifier() {
        return identifier;
    }

}