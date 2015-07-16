package org.tsers.junitquest;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Contains the methods the instrumented bytecode will call
 */

public class Logger {

    private static ArrayList<LoggerState> path = new ArrayList<>();

    private static final int MAX_TIME_BEFORE_ABORT_INVOCATION = 1000;
    private static final int MAX_PATH_SIZE = 500;

    public static void logBEGINMETHOD(String methodDesc) {
        path.add(new LoggerState(Jutil.BEGIN_METHOD_OPCODE, methodDesc));
    }

    public static void logILOAD(int operand) {
        path.add(new LoggerState(Opcodes.ILOAD, operand));
    }

    public static void logLLOAD(int operand) {
        path.add(new LoggerState(Opcodes.LLOAD, operand));
    }

    public static void logACONST_NULL() {
        path.add(new LoggerState(Opcodes.ACONST_NULL, null));
    }

    public static void logICONST_M1() {
        path.add(new LoggerState(Opcodes.ICONST_M1, null));
    }

    public static void logICONST_0() {
        path.add(new LoggerState(Opcodes.ICONST_0, null));
    }

    public static void logICONST_1() {
        path.add(new LoggerState(Opcodes.ICONST_1, null));
    }

    public static void logICONST_2() {
        path.add(new LoggerState(Opcodes.ICONST_2, null));
    }

    public static void logICONST_3() {
        path.add(new LoggerState(Opcodes.ICONST_3, null));
    }

    public static void logICONST_4() {
        path.add(new LoggerState(Opcodes.ICONST_4, null));
    }

    public static void logICONST_5() {
        path.add(new LoggerState(Opcodes.ICONST_5, null));
    }

    public static void logLCONST_0() {
        path.add(new LoggerState(Opcodes.LCONST_0, null));
    }

    public static void logLCONST_1() {
        path.add(new LoggerState(Opcodes.LCONST_1, null));
    }


    public static void logBIPUSH(int operand) {
        path.add(new LoggerState(Opcodes.BIPUSH, operand));
    }

    public static void logSIPUSH(int operand) {
        path.add(new LoggerState(Opcodes.SIPUSH, operand));
    }

    public static void logIADD() {
        path.add(new LoggerState(Opcodes.IADD, null));
    }

    public static void logLADD() {
        path.add(new LoggerState(Opcodes.IADD, null));
    }

    public static void logISUB() {
        path.add(new LoggerState(Opcodes.ISUB, null));
    }

    public static void logLSUB() {
        path.add(new LoggerState(Opcodes.LSUB, null));
    }


    public static void logIDIV() {
        path.add(new LoggerState(Opcodes.IDIV, null));
    }

    public static void logIMUL() {
        path.add(new LoggerState(Opcodes.IMUL, null));
    }

    public static void logIREM() {
        path.add(new LoggerState(Opcodes.IREM, null));
    }

    public static void logINEG() {
        path.add(new LoggerState(Opcodes.INEG, null));
    }

    public static void logLNEG() {
        path.add(new LoggerState(Opcodes.LNEG, null));
    }

    public static void logLMUL() {
        path.add(new LoggerState(Opcodes.LMUL, null));
    }

    public static void logLDIV() {
        path.add(new LoggerState(Opcodes.LDIV, null));
    }

    public static void logLCMP() {
        path.add(new LoggerState(Opcodes.LCMP, null));
    }

    public static void logISTORE(int operand) {
        path.add(new LoggerState(Opcodes.ISTORE, operand));
    }

    public static void logLSTORE(int operand) {
        path.add(new LoggerState(Opcodes.LSTORE, operand));
    }

    public static void logIFNE(String label) {
        path.add(new LoggerState(Opcodes.IFNE, label));
    }

    public static void logIFLE(String label) {
        path.add(new LoggerState(Opcodes.IFLE, label));
    }

    public static void logIFGE(String label) {
        path.add(new LoggerState(Opcodes.IFGE, label));
    }

    public static void logIFGT(String label) {
        path.add(new LoggerState(Opcodes.IFGT, label));
    }

    public static void logIFLT(String label) {
        path.add(new LoggerState(Opcodes.IFLT, label));
    }

    public static void logIF_ICMPNE(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPNE, label));
    }

    public static void logIF_ICMPGE(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPGE, label));
    }

    public static void logIF_ICMPGT(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPGT, label));
    }

    public static void logIF_ICMPLE(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPLE, label));
    }

    public static void logIF_ICMPLT(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPLT, label));
    }

    public static void logIF_ICMPEQ(String label) {
        path.add(new LoggerState(Opcodes.IF_ICMPEQ, label));
    }

    public static void logIF_ACMPEQ(String label) {
        path.add(new LoggerState(Opcodes.IF_ACMPEQ, label));
    }

    public static void logIF_ACMPNE(String label) {
        path.add(new LoggerState(Opcodes.IF_ACMPNE, label));
    }


    public static void logGOTO(String label) {
        path.add(new LoggerState(Opcodes.GOTO, label));
    }

    public static void logTABLESWITCH(int min, int max) {
        path.add(new LoggerState(Opcodes.TABLESWITCH, Arrays.asList(min, max)));
    }

    public static void logRETURN() {
        path.add(new LoggerState(Opcodes.RETURN, null));
    }

    public static void logIRETURN() {
        path.add(new LoggerState(Opcodes.IRETURN, null));
    }

    public static void logARETURN() {
        path.add(new LoggerState(Opcodes.ARETURN, null));
    }

    public static void logLRETURN() {
        path.add(new LoggerState(Opcodes.LRETURN, null));
    }

    public static void logGETSTATIC(Object descr) {
        path.add(new LoggerState(Opcodes.GETSTATIC, descr));
    }

    public static void logLDC(int cst) {
        path.add(new LoggerState(Opcodes.LDC, cst));
    }

    public static void logLDC(long cst) {
        path.add(new LoggerState(Opcodes.LDC, cst));
    }

    public static void logLDC(String cst) {
        path.add(new LoggerState(Opcodes.LDC, cst));
    }

    public static void logLOOKUPSWITCH(List<Integer> keys, List<String> labels, String dflt) {
        path.add(new LoggerState(Opcodes.LOOKUPSWITCH, Arrays.asList(keys, labels, dflt)));
    }

    public static void logINVOKEVIRTUAL(Object descr, int index) {
        path.add(new LoggerState(Opcodes.INVOKEVIRTUAL, descr, index));
    }

    public static void logINVOKESPECIAL(Object descr, int index) {
        path.add(new LoggerState(Opcodes.INVOKESPECIAL, descr, index));
    }

    public static void logINVOKESTATIC(Object descr, int index) {
        path.add(new LoggerState(Opcodes.INVOKESTATIC, descr, index));
    }

    public static void logINVOKEINTERFACE(Object desc, int index) {
        path.add(new LoggerState(Opcodes.INVOKEINTERFACE, desc, index));
    }

    public static void logNEW(Object descr) {
        path.add(new LoggerState(Opcodes.NEW, descr));
    }

    public static void logNEWARRAY(Object descr) {
        path.add(new LoggerState(Opcodes.NEWARRAY, descr));
    }

    public static void logANEWARRAY() {
        path.add(new LoggerState(Opcodes.ANEWARRAY, null));
    }

    public static void logPOP() {
        path.add(new LoggerState(Opcodes.POP, null));
    }

    public static void logPOP2() {
        path.add(new LoggerState(Opcodes.POP2, null));
    }

    public static void logDUP() {
        path.add(new LoggerState(Opcodes.DUP, null));
    }

    public static void logDUP_X1() {
        path.add(new LoggerState(Opcodes.DUP_X1, null));
    }

    public static void logAASTORE() {
        path.add(new LoggerState(Opcodes.AASTORE, null));
    }

    public static void logAALOAD() {
        path.add(new LoggerState(Opcodes.AALOAD, null));
    }


    public static void logASTORE(int operand) {
        path.add(new LoggerState(Opcodes.ASTORE, operand));
    }

    public static void logALOAD(int operand) {
        path.add(new LoggerState(Opcodes.ALOAD, operand));
    }

    public static void logIALOAD() {
        path.add(new LoggerState(Opcodes.IALOAD, null));
    }

    public static void IASTORE() {
        path.add(new LoggerState(Opcodes.IASTORE, null));
    }

    public static void logBALOAD() {
        path.add(new LoggerState(Opcodes.BALOAD, null));
    }

    public static void logCASTORE() {
        path.add(new LoggerState(Opcodes.CASTORE, null));
    }

    public static void logCALOAD() {
        path.add(new LoggerState(Opcodes.CALOAD, null));
    }

    public static void logBASTORE() {
        path.add(new LoggerState(Opcodes.BASTORE, null));
    }

    public static void logIAND() {
        path.add(new LoggerState(Opcodes.IAND, null));
    }

    public static void logIOR() {
        path.add(new LoggerState(Opcodes.IOR, null));
    }

    public static void logIXOR() {
        path.add(new LoggerState(Opcodes.IXOR, null));
    }

    public static void logISHL() {
        path.add(new LoggerState(Opcodes.ISHL, null));
    }

    public static void logINSTANCEOF(String instance) {
        path.add(new LoggerState(Opcodes.INSTANCEOF, instance));
    }

    public static void logIFEQ(String label) {
        path.add(new LoggerState(Opcodes.IFEQ, label));
    }

    public static void logLabel(String label) {
        path.add(new LoggerState(-1, label));
    }

    public static void logCHECKCAST(String desc) {
        path.add(new LoggerState(Opcodes.CHECKCAST, desc));
    }

    public static void logIFNULL(String label) {
        path.add(new LoggerState(Opcodes.IFNULL, label));
    }

    public static void logIFNONNULL(String label) {
        path.add(new LoggerState(Opcodes.IFNONNULL, label));
    }

    public static void logI2L() {
        path.add(new LoggerState(Opcodes.I2L, null));
    }

    public static void logI2C() {
        path.add(new LoggerState(Opcodes.I2C, null));
    }

    public static void logI2B() {
        path.add(new LoggerState(Opcodes.I2B, null));
    }

    public static void logPUTSTATIC() {
        path.add(new LoggerState(Opcodes.PUTSTATIC, null));
    }

    public static void logGETFIELD(String owner, String desc, String name, int index) {
        path.add(new LoggerState(Opcodes.GETFIELD, Arrays.asList(owner, desc, name), index));
    }

    public static void logPUTFIELD(String owner, String desc, String name) {
        path.add(new LoggerState(Opcodes.PUTFIELD, Arrays.asList(owner, desc, name)));
    }

    public static void logARRAYLENGTH() {
        path.add(new LoggerState(Opcodes.ARRAYLENGTH, null));
    }

    public static void logATHROW() {
        path.add(new LoggerState(Opcodes.ATHROW, null));
    }

    public static void logIINC() {
        path.add(new LoggerState(Opcodes.IINC, null));
    }

    public static void logLXOR() {
        path.add(new LoggerState(Opcodes.LXOR, null));
    }

    public static void logIUSHR() {
        path.add(new LoggerState(Opcodes.IUSHR, null));
    }


    public static void logL2I() {
        path.add(new LoggerState(Opcodes.L2I, null));
    }

    public static void logMONITORENTER() {
        path.add(new LoggerState(Opcodes.MONITORENTER, null));
    }

    public static void logMONITOREXIT() {
        path.add(new LoggerState(Opcodes.MONITOREXIT, null));
    }

    public static void logD2F() {
        path.add(new LoggerState(Opcodes.D2F, null));
    }

    public static void logD2I() {
        path.add(new LoggerState(Opcodes.D2I, null));
    }

    public static void logD2L() {
        path.add(new LoggerState(Opcodes.D2L, null));
    }

    public static void logDADD() {
        path.add(new LoggerState(Opcodes.DADD, null));
    }

    public static void logDALOAD() {
        path.add(new LoggerState(Opcodes.DALOAD, null));
    }

    public static void logDASTORE() {
        path.add(new LoggerState(Opcodes.DASTORE, null));
    }

    public static void logDCMPG() {
        path.add(new LoggerState(Opcodes.DCMPG, null));
    }

    public static void logDCMPL() {
        path.add(new LoggerState(Opcodes.DCMPL, null));
    }

    public static void logDCONST_0() {
        path.add(new LoggerState(Opcodes.DCONST_0, null));
    }

    public static void logDCONST_1() {
        path.add(new LoggerState(Opcodes.DCONST_1, null));
    }

    public static void logDDIV() {
        path.add(new LoggerState(Opcodes.DDIV, null));
    }

    public static void logDLOAD(int operand) {
        path.add(new LoggerState(Opcodes.DLOAD, operand));
    }

    public static void logDMUL() {
        path.add(new LoggerState(Opcodes.DMUL, null));
    }

    public static void logDNEG() {
        path.add(new LoggerState(Opcodes.DNEG, null));
    }

    public static void logDREM() {
        path.add(new LoggerState(Opcodes.DREM, null));
    }

    public static void logDRETURN() {
        path.add(new LoggerState(Opcodes.DRETURN, null));
    }

    public static void logDSTORE(int operand) {
        path.add(new LoggerState(Opcodes.DSTORE, operand));
    }

    public static void logDSUB() {
        path.add(new LoggerState(Opcodes.DSUB, null));
    }

    public static void logF2D() {
        path.add(new LoggerState(Opcodes.F2D, null));
    }

    public static void logF2I() {
        path.add(new LoggerState(Opcodes.F2I, null));
    }

    public static void logF2L() {
        path.add(new LoggerState(Opcodes.F2L, null));
    }

    public static void logFADD() {
        path.add(new LoggerState(Opcodes.FADD, null));
    }

    public static void logFALOAD() {
        path.add(new LoggerState(Opcodes.FALOAD, null));
    }

    public static void logFASTORE() {
        path.add(new LoggerState(Opcodes.FASTORE, null));
    }

    public static void logFCMPG() {
        path.add(new LoggerState(Opcodes.FCMPG, null));
    }

    public static void logFCMPL() {
        path.add(new LoggerState(Opcodes.FCMPL, null));
    }

    public static void logFCONST_0() {
        path.add(new LoggerState(Opcodes.FCONST_0, null));
    }

    public static void logFCONST_1() {
        path.add(new LoggerState(Opcodes.FCONST_1, null));
    }

    public static void logFCONST_2() {
        path.add(new LoggerState(Opcodes.FCONST_2, null));
    }

    public static void logFDIV() {
        path.add(new LoggerState(Opcodes.FDIV, null));
    }

    public static void logFLOAD(int operand) {
        path.add(new LoggerState(Opcodes.FLOAD, operand));
    }

    public static void logFMUL() {
        path.add(new LoggerState(Opcodes.FMUL, null));
    }

    public static void logFNEG() {
        path.add(new LoggerState(Opcodes.FNEG, null));
    }

    public static void logFREM() {
        path.add(new LoggerState(Opcodes.FREM, null));
    }

    public static void logFRETURN() {
        path.add(new LoggerState(Opcodes.FRETURN, null));
    }

    public static void logFSTORE(int operand) {
        path.add(new LoggerState(Opcodes.FSTORE, operand));
    }

    public static void logFSUB() {
        path.add(new LoggerState(Opcodes.FSUB, null));
    }


    public static void logStackTop(int stackTop, int i) {
        LoggerState state =
                path.stream()
                        .filter(p -> p.getIdentifier() == i)
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("Cannot find method when logging stack top after returning from method"));

        state.setReturnValue(stackTop);
    }

    public static ExecutionPath getExecutionPath(AccessibleObject ao, Object... params) throws InfiniteLoopException, InterruptedException {


        //Let's run the invocation on a separate thread since whe have actually no idea what we are running
        Thread r = new Thread() {
            @Override
            public void run() {
                try {
                    invokeAccessibleObject(ao, params);
                } catch (Exception e) {

                }
            }
        };

        r.start();
        r.join(MAX_TIME_BEFORE_ABORT_INVOCATION);
        if (r.isAlive()) {
            //WE HAVE TO STOP IT! It could be in a infinite loop and the runnable code is not in our hands
            r.stop();
            throw new InfiniteLoopException();
        }

        if (path.size() > MAX_PATH_SIZE) {
            throw new InfiniteLoopException();
        }

        return new ExecutionPath(path);
    }

    private static void invokeAccessibleObject(AccessibleObject ao, Object[] params) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (ao instanceof Method) {
            invokeMethod((Method) ao, params);
        } else if (ao instanceof Constructor) {
            invokeConstructor((Constructor) ao, params);
        }
    }

    private static void invokeConstructor(Constructor ao, Object[] params) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Logger.beginMethodLogging();
        Object callParams[] = shiftLeftIfNotStatic(ao, params);
        ((Constructor) ao).newInstance(callParams);
    }

    private static void invokeMethod(Method ao, Object[] params) throws IllegalAccessException, InvocationTargetException {
        Method m = ao;
        Object classInstance = getClassInstance(m, params);
        Object callParams[] = shiftLeftIfNotStatic(m, params);
        Logger.beginMethodLogging();
        m.invoke(classInstance, callParams);
    }

    public static Object[] shiftLeftIfNotStatic(AccessibleObject m, Object[] params) {
        if (Jutil.isStatic(m)) {
            return params;
        }
        return Jutil.shiftLeft(params);
    }

    private static Object getClassInstance(Method m, Object[] params) {
        if (Jutil.isStatic(m)) {
            return null;
        }
        return params[0];
    }

    private static void beginMethodLogging() {
        path = new ArrayList<>();
    }


}
