package org.tsers.junitquest;

import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExecutionPath {


    private final ArrayList<LoggerState> states;

    public ExecutionPath(List<LoggerState> states) {
        this.states = new ArrayList<>(states);
    }

    public List<LoggerState> getStates() {
        return states;
    }

    public List<String> getLabels() {
        return getStates().stream()
                .filter(s -> s.getOpcode()== Jutil.LABEL_OPCODE)
                .map(s -> (String) s.getOperand())
                .collect(Collectors.toList());
    }


    public ExecutionPath stripBeforeBEGINMETHOD(String methodInfo) {
        ArrayList<LoggerState> methodStates = new ArrayList<>();
        boolean methodFound = false;
        for (int i = 0; i < states.size(); i++) {
            if (methodFound) {
                methodStates.add(states.get(i));
            }
            if (states.get(i).getOpcode() == Jutil.BEGIN_METHOD_OPCODE &&
                    states.get(i).getOperand().equals(methodInfo)) {
                methodFound = true;
            }
        }
        return new ExecutionPath(methodStates);
    }

    public ExecutionPath removeSubroutineStates(List<String> methodLabels) {

        ArrayList<LoggerState> methodStates = new ArrayList<>();
        boolean insideMethod = false;
        int subCall = -1;
        for (LoggerState state : states) {
            if (state.getOpcode() == Jutil.LABEL_OPCODE) {
                String label = (String) state.getOperand();
                if (methodLabels.contains(label)) {
                    insideMethod = true;
                    subCall = 0;
                } else {
                    if (insideMethod) {
                        insideMethod = false;
                        subCall = 1;
                    }
                }
            }
            if (insideMethod) {
                if (state.getOpcode() > Jutil.BEGIN_METHOD_OPCODE) {
                    methodStates.add(state);
                }
            } else if (isReturnOpcode(state.getOpcode()) && subCall > 0) {
                subCall--;
                insideMethod = true;
            }

        }

        for (LoggerState state : methodStates) {
            if (state.getOpcode() == Jutil.LABEL_OPCODE) {
                String label = (String) state.getOperand();
                if (!methodLabels.contains(label)) {
                    throw new RuntimeException("Cannot remove subroutine path");
                }
            }
        }

        return new ExecutionPath(methodStates);
    }

    public static boolean isReturnOpcode(int opcode) {
        return opcode == Opcodes.ARETURN || opcode == Opcodes.IRETURN ||
                opcode == Opcodes.RETURN || opcode == Opcodes.LRETURN ||
                opcode == Opcodes.DRETURN || opcode == Opcodes.FRETURN;
    }

}
