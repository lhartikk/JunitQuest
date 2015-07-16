package org.tsers.junitquest;

import org.objectweb.asm.Opcodes;
import org.tsers.junitquest.expr.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Concoler {

    public ExprNode concole(ExecutionPath executionPath, ExprNode expr) {

        List<LoggerState> states = executionPath.getStates();
        for (int i = states.size() - 1; i >= 0; i--) {
            LoggerState currentState = states.get(i);
            Optional<LoggerState> previousState = getNextState(states, i);
            Function<ExprNode, ExprNode> concoleStepAction = concoleStepAction(currentState, previousState);
            expr = concoleStepAction.apply(expr);
        }
        return expr;

    }

    public static Optional<LoggerState> getNextState(List<LoggerState> states, int i) {
        int nextStateIndex = i + 1;
        if (nextStateIndex >= states.size()) {
            return Optional.empty();
        }
        return Optional.of(states.get(nextStateIndex));
    }

    private static boolean isLOAD(int opcode) {
        return Arrays.asList(Opcodes.ILOAD, Opcodes.LLOAD, Opcodes.DLOAD, Opcodes.FLOAD, Opcodes.ALOAD).contains(opcode);
    }

    private static boolean isSTORE(int opcode) {
        return Arrays.asList(Opcodes.ISTORE, Opcodes.LSTORE, Opcodes.DSTORE, Opcodes.FSTORE, Opcodes.ASTORE).contains(opcode);
    }

    private static boolean isADD(int opcode) {
        return Arrays.asList(Opcodes.IADD, Opcodes.LADD, Opcodes.DADD, Opcodes.FADD).contains(opcode);
    }

    private static boolean isSUB(int opcode) {
        return Arrays.asList(Opcodes.ISUB, Opcodes.LSUB, Opcodes.DSUB, Opcodes.FSUB).contains(opcode);
    }

    private static boolean isMUL(int opcode) {
        return Arrays.asList(Opcodes.IMUL, Opcodes.LMUL, Opcodes.DMUL, Opcodes.FMUL).contains(opcode);
    }

    private static boolean isDIV(int opcode) {
        return Arrays.asList(Opcodes.IDIV, Opcodes.LDIV, Opcodes.DDIV, Opcodes.FDIV).contains(opcode);
    }

    private static boolean isNEG(int opcode) {
        return Arrays.asList(Opcodes.INEG, Opcodes.LNEG, Opcodes.DNEG, Opcodes.FNEG).contains(opcode);
    }

    private static boolean isXRETURN(int opcode) {
        return Arrays.asList(Opcodes.IRETURN, Opcodes.DRETURN, Opcodes.FRETURN, Opcodes.ARETURN).contains(opcode);
    }

    private static boolean isPUSH(int opcode) {
        return Arrays.asList(Opcodes.BIPUSH, Opcodes.SIPUSH).contains(opcode);
    }

    private static boolean isNO_ACTION(int opcode) {
        return Arrays.asList(Opcodes.POP, Opcodes.LOOKUPSWITCH, Opcodes.TABLESWITCH, Opcodes.INVOKESPECIAL,
                Opcodes.PUTSTATIC, Opcodes.PUTFIELD, Opcodes.GOTO, Opcodes.DUP, Opcodes.NEW, Opcodes.RETURN,
                Opcodes.ATHROW, Opcodes.I2L, Opcodes.I2S, Opcodes.I2F, Opcodes.I2D, Opcodes.I2B, Opcodes.I2B,
                Opcodes.L2D, Opcodes.L2F, Opcodes.L2I, Opcodes.F2D, Opcodes.F2I, Opcodes.F2L,
                Jutil.LABEL_OPCODE, Jutil.BEGIN_METHOD_OPCODE).contains(opcode);
    }

    private Function<ExprNode, ExprNode> concoleStepAction(LoggerState currentState, Optional<LoggerState> previousState) {


        boolean jumpFollowed = isJumpFollowed(currentState, previousState);
        int opcode = currentState.getOpcode();

        if (isLOAD(opcode)) {
            return ILOAD(currentState);
        } else if (isSTORE(opcode)) {
            return ISTORE(currentState);
        } else if (isADD(opcode)) {
            return IADD();
        } else if (isSUB(opcode)) {
            return ISUB();
        } else if (isMUL(opcode)) {
            return IMUL();
        } else if (isDIV(opcode)) {
            return IDIV();
        } else if (isNEG(opcode)) {
            return NO_ACTION();
        } else if (opcode == Opcodes.ACONST_NULL) {
            return NULLPUSH();
        } else if (opcode == Opcodes.ICONST_M1) {
            return INTEGERPUSH(-1);
        } else if (opcode == Opcodes.ICONST_0) {
            return INTEGERPUSH(0);
        } else if (opcode == Opcodes.ICONST_1) {
            return INTEGERPUSH(1);
        } else if (opcode == Opcodes.ICONST_2) {
            return INTEGERPUSH(2);
        } else if (opcode == Opcodes.ICONST_3) {
            return INTEGERPUSH(3);
        } else if (opcode == Opcodes.ICONST_4) {
            return INTEGERPUSH(4);
        } else if (opcode == Opcodes.ICONST_5) {
            return INTEGERPUSH(5);
        } else if (opcode == Opcodes.LCONST_0) {
            return INTEGERPUSH(0);
        } else if (opcode == Opcodes.LCONST_1) {
            return INTEGERPUSH(1);
        } else if (opcode == Opcodes.FCONST_0) {
            return INTEGERPUSH(0);
        } else if (opcode == Opcodes.FCONST_1) {
            return INTEGERPUSH(1);
        } else if (opcode == Opcodes.FCONST_2) {
            return INTEGERPUSH(2);
        } else if (opcode == Opcodes.DCONST_0) {
            return INTEGERPUSH(0);
        } else if (opcode == Opcodes.DCONST_1) {
            return INTEGERPUSH(1);
        } else if (isPUSH(opcode)) {
            return INTEGERPUSH((int) currentState.getOperand());
        } else if (isCMP(opcode)) {
            return Jutil.applyRecursively(cmp);
        } else if (opcode == Opcodes.IFNE) {
            return IFNE(jumpFollowed);
        } else if (opcode == Opcodes.IFEQ) {
            return IFNE(!jumpFollowed);
        } else if (opcode == Opcodes.IFGE) {
            return IFGE(jumpFollowed);
        } else if (opcode == Opcodes.IFGT) {
            return IFGT(jumpFollowed);
        } else if (opcode == Opcodes.IFLE) {
            return IFLE(jumpFollowed);
        } else if (opcode == Opcodes.IFLT) {
            return IFLT(jumpFollowed);
        } else if (opcode == Opcodes.IF_ACMPNE) {
            return IF_ICMPEQ(jumpFollowed);
        } else if (opcode == Opcodes.IF_ACMPEQ) {
            return IF_ICMPEQ(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPNE) {
            return IF_ICMPNE(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPEQ) {
            return IF_ICMPEQ(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPGE) {
            return IF_ICMPGE(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPGT) {
            return IF_ICMPGT(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPLE) {
            return IF_ICMPLE(jumpFollowed);
        } else if (opcode == Opcodes.IF_ICMPLT) {
            return IF_ICMPLT(jumpFollowed);
        } else if (opcode == Opcodes.IFNULL) {
            return IFNULL(jumpFollowed);
        } else if (opcode == Opcodes.IFNONNULL) {
            return IFNONNULL(jumpFollowed);
        } else if (opcode == Opcodes.INVOKEVIRTUAL) {
            return createInvokeMethodFunction(currentState, previousState, true);
        } else if (opcode == Opcodes.INVOKESTATIC) {
            return createInvokeMethodFunction(currentState, previousState, false);
        } else if (opcode == Opcodes.INVOKEINTERFACE) {
            return createInvokeMethodFunction(currentState, previousState, true);
        } else if (opcode == Opcodes.GETSTATIC) {
            return ILOAD(nonMatchingState());
        } else if (opcode == Opcodes.GETFIELD) {
            return GETFIELD(currentState);
        } else if (opcode == Opcodes.LDC) {
            return LDC(currentState);
        } else if (isXRETURN(opcode)) {
            return GENERAL_RETURN(previousState);
        } else if (opcode == Opcodes.INSTANCEOF) {
            return INSTANCEOF(currentState);
        } else if (opcode == Opcodes.CHECKCAST) {
            return CHECKCAST(currentState);
        } else if (isNO_ACTION(opcode)) {
            return NO_ACTION();
        }
        return NO_ACTION();
    }

    private Function<ExprNode, ExprNode> CHECKCAST(LoggerState currentState) {
        ExprNode stackTop = new StackNode(0);
        ExprNode instanceOfNode = new InstanceOfNode(stackTop, ((String) currentState.getOperand()).replace("/", "."));
        ExprNode instanceOfEq = new NotEqualNode(Arrays.asList(new IntNode(0), instanceOfNode));
        return AND(instanceOfEq);
    }

    private Function<ExprNode, ExprNode> INSTANCEOF(LoggerState currentState) {
        String instanceType = (String) currentState.getOperand();
        return Jutil.applyRecursively(Jutil.curry(instanceOf, instanceType));
    }

    private Function<ExprNode, ExprNode> LDC(LoggerState currentState) {
        if (currentState.getOperand() instanceof Integer) {
            return INTEGERPUSH((Integer) currentState.getOperand());
        } else if (currentState.getOperand() instanceof Long) {
            return INTEGERPUSH(new BigDecimal((Long) currentState.getOperand()).intValue());
        } else if (currentState.getOperand() instanceof String) {
            return INTEGERPUSH(99999);
        } else {
            return NO_ACTION();
        }
    }

    private Function<ExprNode, ExprNode> GETFIELD(LoggerState currentState) {
        List<String> ids = (List<String>) currentState.getOperand();
        ExprNode getFieldNode = new GetFieldNode(new StackNode(0), ids.get(0), ids.get(2), currentState.getReturnValue());
        return Jutil.applyRecursively(Jutil.curry(getfield, getFieldNode));
    }

    private Function<ExprNode, ExprNode> IFNONNULL(boolean jumpFollowed) {
        if (jumpFollowed) {
            return NO_ACTION();
        } else {
            ExprNode condition = UtilEquation.stackTopNull();
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFNULL(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopNull();
            return AND(condition);
        } else {
            return NO_ACTION();
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPLT(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(LessThanNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(GreaterThanEqNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPLE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(LessThanEqNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(GreaterThanNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPGT(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(GreaterThanEqNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(LessThanNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPGE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(GreaterThanNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(LessThanEqNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPNE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(NotEqualNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(EqualNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IF_ICMPEQ(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopsCondition(EqualNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopsCondition(NotEqualNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFLT(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopConditionZero(LessThanNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopConditionZero(GreaterThanEqNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFLE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopConditionZero(LessThanEqNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopConditionZero(GreaterThanNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFGT(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopConditionZero(GreaterThanNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopConditionZero(LessThanEqNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFGE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopConditionZero(GreaterThanEqNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopConditionZero(LessThanNode.class);
            return AND(condition);
        }
    }

    private Function<ExprNode, ExprNode> IFNE(boolean jumpFollowed) {
        if (jumpFollowed) {
            ExprNode condition = UtilEquation.stackTopConditionZero(NotEqualNode.class);
            return AND(condition);
        } else {
            ExprNode condition = UtilEquation.stackTopConditionZero(EqualNode.class);
            return AND(condition);
        }
    }

    private static boolean isCMP(int opcode) {
        return opcode == Opcodes.LCMP || opcode == Opcodes.DCMPL || opcode == Opcodes.DCMPG
                || opcode == Opcodes.FCMPL || opcode == Opcodes.FCMPG;
    }

    private static boolean isSubroutineLogged(Optional<LoggerState> previousState) {
        if (previousState.isPresent()) {
            return previousState.get().getOpcode() == Jutil.BEGIN_METHOD_OPCODE;
        }
        return false;
    }

    public static boolean isJumpFollowed(LoggerState currentState, Optional<LoggerState> nextState) {
        if (!nextState.isPresent()) {
            return false;
        }
        if (currentState.getOperand() == null) {
            return false;
        }
        if (nextState.get().getOperand() == null) {
            return false;
        }
        String jumpLabel = currentState.getOperand().toString();
        String prevLabel = nextState.get().getOperand().toString();
        return jumpLabel.equals(prevLabel);
    }

    public static BiFunction<ExprNode, ExprNode, ExprNode> getfield = (node, getFieldNode) -> {
        if (node instanceof StackNode) {
            int locationOnStack = ((StackNode) node).getValue();
            if (locationOnStack == 0) {
                return getFieldNode;
            }
        }
        return node;
    };

    public static BiFunction<ExprNode, String, ExprNode> instanceOf = (node, instanceType) -> {
        if (node instanceof StackNode) {
            int locationOnStack = ((StackNode) node).getValue();
            if (locationOnStack == 0) {
                return new InstanceOfNode(new StackNode(0), instanceType);
            }
        }
        return node;
    };
    public static Function<ExprNode, ExprNode> cmp = node -> {
        if (Jutil.isSimpleEquation(node)) {
            IntNode cnode = (IntNode) node.getChildren().stream().filter(n -> n instanceof IntNode)
                    .findFirst().get();
            if (node instanceof EqualNode) {
                if (cnode.getValue() == -1) {
                    return UtilEquation.stackTopsCondition(LessThanNode.class);
                } else if (cnode.getValue() == 0) {
                    return UtilEquation.stackTopsCondition(EqualNode.class);
                } else if (cnode.getValue() == 1) {
                    return UtilEquation.stackTopsCondition(GreaterThanNode.class);
                }
            } else if (node instanceof NotEqualNode) {
                if (cnode.getValue() == -1) {
                    return UtilEquation.stackTopsCondition(GreaterThanNode.class);
                } else if (cnode.getValue() == 0) {
                    return UtilEquation.stackTopsCondition(NotEqualNode.class);
                } else if (cnode.getValue() == 1) {
                    return UtilEquation.stackTopsCondition(LessThanNode.class);
                }
            }
        }
        return node;
    };

    private static Function<ExprNode, ExprNode> NO_ACTION() {
        return e -> e;
    }

    private static Function<ExprNode, ExprNode> NULLPUSH() {
        return Jutil.applyRecursively(nullpushTransformation());
    }

    private static Function<ExprNode, ExprNode> nullpushTransformation() {
        return node ->
        {
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return new NullNode();
                } else {
                    return new StackNode(locationOnStack - 1);
                }
            } else {
                return node;
            }

        };
    }


    private static Function<ExprNode, ExprNode> GENERAL_RETURN(Optional<LoggerState> previousState) {
        //not last of the logger states
        if (previousState.isPresent()) {
            return ISTORE(nonMatchingState());
        }
        return NO_ACTION();
    }

    private static Function<ExprNode, ExprNode> createInvokeMethodFunction(
            LoggerState currentState, Optional<LoggerState> previousState, boolean hasInstance) {

        boolean subRoutineLogged = isSubroutineLogged(previousState);
        String myMethodIdentifier[] = ((String) currentState.getOperand()).split("\\|");
        String classPackage = myMethodIdentifier[0];
        String methodName = myMethodIdentifier[1];
        String methodDesc = myMethodIdentifier[2];

        int numberofLocalParameters = Jutil.descToParameterTypes(methodDesc).length;

        if (hasInstance) {
            numberofLocalParameters++;
        }

        List<ExprNode> children = IntStream.range(0, numberofLocalParameters)
                .mapToObj(p -> new StackNode(p))
                .collect(Collectors.toList());


        ExprNode invokeVirtualNode = new InvokeVirtualNode(children, classPackage, methodName, methodDesc, currentState.returnValue);

        return Jutil.applyRecursively(Jutil.curry(invokeVirtual(subRoutineLogged), invokeVirtualNode));
    }

    private static LoggerState nonMatchingState() {
        return new LoggerState(0, new Integer(50));
    }

    private static Function<ExprNode, ExprNode> ILOAD(LoggerState state) {
        return node -> {
            Function<ExprNode, ExprNode> iloadTransformation = iloadTransformation(state);
            return Jutil.applyRecursively(iloadTransformation).apply(node);
        };
    }

    private static Function<ExprNode, ExprNode> iloadTransformation(LoggerState state) {
        return node -> {
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return new LocalNode((Integer) state.getOperand());
                } else {
                    return new StackNode(locationOnStack - 1);
                }
            }
            return node;
        };
    }

    private static Function<ExprNode, ExprNode> ISTORE(LoggerState state) {
        return node -> {
            Function<ExprNode, ExprNode> istoreTransformation = istoreTransformation(state);
            return Jutil.applyRecursively(istoreTransformation).apply(node);
        };
    }

    private static Function<ExprNode, ExprNode> istoreTransformation(LoggerState state) {
        return node -> {
            if (node instanceof LocalNode) {
                int locationOnLocal = ((LocalNode) node).getValue();
                if (locationOnLocal == (Integer) state.getOperand()) {
                    return new StackNode(0);
                } else {
                    return node;
                }
            } else if (node instanceof StackNode) {
                return new StackNode(((StackNode) node).getValue() + 1);
            } else {
                return node;
            }
        };
    }

    private Function<ExprNode, ExprNode> IADD() {
        return node ->
                Jutil.applyRecursively(arithmeticTransformation(AddNode.class))
                        .apply(node);
    }

    private Function<ExprNode, ExprNode> ISUB() {
        return node -> Jutil.applyRecursively(isubTransformation()).apply(node);
    }

    private Function<ExprNode, ExprNode> isubTransformation() {
        return node -> {
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return new AddNode(Arrays.asList(
                            new MinusNode(new StackNode(0)), new StackNode(1))
                    );
                } else {
                    return new StackNode(locationOnStack + 1);
                }
            } else {
                return node;
            }

        };
    }

    private Function<ExprNode, ExprNode> IMUL() {
        return node ->
                Jutil.applyRecursively(arithmeticTransformation(MultiplicationNode.class))
                        .apply(node);
    }

    private Function<ExprNode, ExprNode> IDIV() {
        return node ->
                Jutil.applyRecursively(arithmeticTransformation(DivisionNode.class))
                        .apply(node);
    }

    private Function<ExprNode, ExprNode> arithmeticTransformation(Class nodeClass) {
        return node -> {
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return Jutil.createNode.apply(nodeClass,
                            Arrays.asList(new StackNode(0), new StackNode(1)));
                } else {
                    return new StackNode(locationOnStack + 1);
                }
            } else {
                return node;
            }
        };
    }

    private Function<ExprNode, ExprNode> INTEGERPUSH(int pushed) {
        return node -> Jutil.applyRecursively(integerpushTransformation(pushed)).apply(node);
    }

    private Function<ExprNode, ExprNode> integerpushTransformation(Integer pushed) {
        return node -> {
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return new IntNode(pushed);
                } else {
                    return new StackNode(locationOnStack - 1);
                }
            } else {
                return node;
            }

        };
    }

    public static BiFunction<ExprNode, ExprNode, ExprNode> invokeVirtual(boolean subroutineLogged) {
        return (node, invokeVirtualNode) -> {
            int incrementStacks = invokeVirtualNode.getChildren().size() - 1;
            if (node instanceof StackNode) {
                int locationOnStack = ((StackNode) node).getValue();
                if (locationOnStack == 0) {
                    return invokeVirtualNode;
                } else {
                    return new StackNode(locationOnStack + incrementStacks);
                }
            }
            if (subroutineLogged) {
                if (node instanceof LocalNode) {
                    int location = ((LocalNode) node).getValue();
                    int virtualNodeParams = invokeVirtualNode.getChildren().size() - 1;
                    if (location <= virtualNodeParams) {
                        return new StackNode(virtualNodeParams - location);
                    } else {
                        return node;
                    }
                }
            }
            return node;
        };
    }

    private Function<ExprNode, ExprNode> AND(ExprNode condition) {
        return node -> new AndNode(Arrays.asList(condition, node));
    }

}