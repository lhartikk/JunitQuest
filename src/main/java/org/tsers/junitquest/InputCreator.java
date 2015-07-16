package org.tsers.junitquest;

import org.objectweb.asm.Opcodes;
import org.tsers.junitquest.expr.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InputCreator {


    public static List<Integer> BRANCH_INSTRUCTIONS =
            Arrays.asList(Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE, Opcodes.IF_ICMPEQ,
                    Opcodes.IF_ICMPGE, Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE, Opcodes.IF_ICMPLT, Opcodes.IF_ICMPNE,
                    Opcodes.IFEQ, Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE, Opcodes.IFLT, Opcodes.IFNE, Opcodes.IFNONNULL,
                    Opcodes.IFNULL, Opcodes.LOOKUPSWITCH, Opcodes.TABLESWITCH, Opcodes.CHECKCAST);

    public static ArrayList<ConcolerInput> createConcolerInputs(ExecutionPath givenPath) {
        ArrayList<ConcolerInput> concolerInputs = new ArrayList<>();

        List<LoggerState> seenStates = new ArrayList<>();

        for (int i = 0; i < givenPath.getStates().size(); i++) {

            LoggerState currentState = givenPath.getStates().get(i);
            Optional<LoggerState> nextState = Concoler.getNextState(givenPath.getStates(), i);
            boolean isJumpFollowed = Concoler.isJumpFollowed(currentState, nextState);
            concolerInputs.addAll(opcodeToConcolerInput(seenStates, currentState, isJumpFollowed));
            seenStates.add(currentState);
        }
        return concolerInputs;
    }

    private static List<ConcolerInput> opcodeToConcolerInput(
            List<LoggerState> seenStates, LoggerState currentState, boolean isJumpFollowed) {
        if (!BRANCH_INSTRUCTIONS.contains(currentState.getOpcode())) {
            return Arrays.asList();
        } else if (currentState.getOpcode() == Opcodes.CHECKCAST) {
            ExprNode stackTop = new StackNode(0);
            ExprNode instanceOfNode = new InstanceOfNode(stackTop, ((String) currentState.getOperand()).replace("/", "."));
            ExprNode instanceOfEq = new NotEqualNode(Arrays.asList(new IntNode(0), instanceOfNode));
            return Arrays.asList(new ConcolerInput(new ExecutionPath(seenStates), instanceOfEq));
        } else {
            return branchOpcodeToConcolerInput(seenStates, currentState, isJumpFollowed);
        }

    }

    private static List<ConcolerInput> branchOpcodeToConcolerInput
            (List<LoggerState> seenStates, LoggerState state, boolean isJumpFollowed) {
        int opcode = state.getOpcode();
        if (opcode == Opcodes.IFNE) {
            return IFNEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFEQ) {
            return IFEQConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFGE) {
            return IFGEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFGT) {
            return IFGTConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFLE) {
            return IFLEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFLT) {
            return IFLTConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPEQ) {
            return IF_ICMPEQConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPNE) {
            return IF_ICMPNEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPGE) {
            return IF_ICMPGEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPGT) {
            return IF_ICMPGTConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPLE) {
            return IF_ICMPLEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPLT) {
            return ICMPLTConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IF_ACMPNE) {
            return IF_ICMPNEConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.LOOKUPSWITCH) {
            return LOOKUPSWITCHConditions(seenStates, state);
        } else if (opcode == Opcodes.TABLESWITCH) {
            return TABLESWITCHConditions(seenStates, state);
        } else if (opcode == Opcodes.IFNONNULL) {
            return IFNONNULLConditions(seenStates, isJumpFollowed);
        } else if (opcode == Opcodes.IFNULL) {
            return IFNULLConditions(seenStates, isJumpFollowed);
        } else {
            return Arrays.asList();
        }
    }

    private static List<ConcolerInput> IFNULLConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList();
        } else {
            return Arrays.asList(stackTopNull(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFNONNULLConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopNull(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList();
        }
    }

    private static List<ConcolerInput> TABLESWITCHConditions(List<LoggerState> seenStates, LoggerState state) {
        Integer min = ((List<Integer>) state.getOperand()).get(0);
        Integer max = ((List<Integer>) state.getOperand()).get(1);

        //also add a value not found in range to match dflt
        return IntStream.range(min, max + 2)
                .mapToObj(c -> {
                    final ExprNode stackNode0 = new StackNode(0);
                    final ExprNode intNode = new IntNode(c);
                    final ExprNode equalNode = new EqualNode(Arrays.asList(stackNode0, intNode));
                    return new ConcolerInput((new ExecutionPath(seenStates)), equalNode);
                })
                .collect(Collectors.toList());
    }

    private static List<ConcolerInput> LOOKUPSWITCHConditions(List<LoggerState> seenStates, LoggerState state) {
        List<ConcolerInput> createdInputs = new ArrayList<>();
        List<Integer> keys = ((List<List>) state.getOperand()).get(0);

        List<ExprNode> notEquals = new ArrayList<>();
        for (Integer key : keys) {
            ExprNode stackNode0 = new StackNode(0);
            ExprNode constantNode = new IntNode(key);
            ExprNode equalNode = new EqualNode(Arrays.asList(stackNode0, constantNode));
            createdInputs.add(new ConcolerInput((new ExecutionPath(seenStates)), equalNode));

            ExprNode notEqualNode = new NotEqualNode(Arrays.asList(stackNode0, constantNode));
            notEquals.add(notEqualNode);
        }

        createdInputs.add(new ConcolerInput((new ExecutionPath(seenStates)), new AndNode(notEquals)));
        return createdInputs;
    }

    private static List<ConcolerInput> ICMPLTConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsCondition(GreaterThanEqNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsCondition(LessThanNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IF_ICMPLEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsCondition(GreaterThanNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsCondition(LessThanEqNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IF_ICMPGTConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsCondition(LessThanEqNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsCondition(GreaterThanNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IF_ICMPGEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsCondition(LessThanNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsCondition(GreaterThanEqNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IF_ICMPNEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsEqual(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsNotEqual(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IF_ICMPEQConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopsNotEqual(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopsEqual(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFLTConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(
                    stackTopConditionZero(GreaterThanEqNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsOne(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(
                    stackTopConditionZero(LessThanNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsMinusOne(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFLEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(
                    stackTopConditionZero(GreaterThanNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsOne(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(
                    stackTopConditionZero(LessThanEqNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsMinusOne(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFGTConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(
                    stackTopConditionZero(LessThanEqNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsMinusOne(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(
                    stackTopConditionZero(GreaterThanNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsOne(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFGEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(
                    stackTopConditionZero(LessThanNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsMinusOne(new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(
                    stackTopConditionZero(GreaterThanEqNode.class, new ExecutionPath(seenStates)),
                    stackTopEqualsOne(new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFEQConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopConditionZero(NotEqualNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopConditionZero(EqualNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static List<ConcolerInput> IFNEConditions(List<LoggerState> seenStates, boolean isJumpFollowed) {
        if (isJumpFollowed) {
            return Arrays.asList(stackTopConditionZero(EqualNode.class, new ExecutionPath(seenStates)));
        } else {
            return Arrays.asList(stackTopConditionZero(NotEqualNode.class, new ExecutionPath(seenStates)));
        }
    }

    private static ConcolerInput stackTopEqualsOne(ExecutionPath seenExecutionPath) {
        ExprNode conditionNode = UtilEquation.stackTopEqualsOne();
        return new ConcolerInput(seenExecutionPath, conditionNode);
    }

    private static ConcolerInput stackTopEqualsMinusOne(ExecutionPath seenExecutionPath) {
        ExprNode conditionNode = UtilEquation.stackTopEqualsMinusOne();
        return new ConcolerInput(seenExecutionPath, conditionNode);
    }

    private static ConcolerInput stackTopConditionZero(Class nodeConditionClass, ExecutionPath seenExecutionPath) {
        ExprNode conditionNode = UtilEquation.stackTopConditionZero(nodeConditionClass);
        return new ConcolerInput(seenExecutionPath, conditionNode);
    }

    private static ConcolerInput stackTopsCondition(Class nodeConditionClass, ExecutionPath seenExecutionPath) {
        ExprNode conditionNode = UtilEquation.stackTopsCondition(nodeConditionClass);
        return new ConcolerInput(seenExecutionPath, conditionNode);
    }

    private static ConcolerInput stackTopNull(ExecutionPath seenExecutionPath) {
        ExprNode equalNode = UtilEquation.stackTopNull();
        return new ConcolerInput(seenExecutionPath, equalNode);
    }

    private static ConcolerInput stackTopsNotEqual(ExecutionPath seenExecutionPath) {
        ExprNode equalNode = UtilEquation.stackTopsCondition(NotEqualNode.class);
        return new ConcolerInput(seenExecutionPath, equalNode);
    }

    private static ConcolerInput stackTopsEqual(ExecutionPath seenExecutionPath) {
        ExprNode equalNode = UtilEquation.stackTopsCondition(EqualNode.class);
        return new ConcolerInput(seenExecutionPath, equalNode);
    }

}
