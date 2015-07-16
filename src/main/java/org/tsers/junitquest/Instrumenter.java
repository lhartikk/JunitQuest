package org.tsers.junitquest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.tsers.junitquest.finder.MyClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


public class Instrumenter {

    private static Map<String, Class> instrumentedClasses = new HashMap<>();
    private static List<MethodNode> methodNodes = new ArrayList<>();
    private static URLClassLoader urlClassLoader;
    private static String LOGGER_LOCATION = "org/tsers/junitquest/Logger";


    public static Class getClass(String packagename) {
        try {
            if (instrumentedClasses.containsKey(packagename)) {
                return instrumentedClasses.get(packagename);
            }
            return urlClassLoader.loadClass(packagename);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class: " + packagename);
        }
    }

    public static void instrumentClasses(List<String> packagenames, String bytecodeLocation) {
        ClassLoader sysloader = ClassLoader.getSystemClassLoader();
        instrumentClasses(packagenames, bytecodeLocation, sysloader);

    }

    private static String getByteCodeLocation(String byteCodeLocation) {
        if (!byteCodeLocation.endsWith(Character.toString(File.separatorChar))) {
            return byteCodeLocation + File.separatorChar;
        }
        return byteCodeLocation;
    }

    public static void instrumentClasses(List<String> packagenames, String rawBytecodeLocation, ClassLoader classLoader) {
        try {
            String bytecodeLocation = getByteCodeLocation(rawBytecodeLocation);
            instrumentedClasses = new HashMap<>();
            methodNodes = new ArrayList<>();
            String byteCodeFolder = "file://" + bytecodeLocation;
            URL[] classLoaderUrls = new URL[]{new URL(byteCodeFolder)};
            urlClassLoader = new URLClassLoader(classLoaderUrls, classLoader);

            Class[] parameters = new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE};
            Method classLoaderMethod = ClassLoader.class.getDeclaredMethod("defineClass", parameters);
            classLoaderMethod.setAccessible(true);

            for (String packagename : packagenames) {
                InputStream stream = new FileInputStream(bytecodeLocation + packagename.replace(".", "/") + ".class");
                byte[] instrumentedByteCode = Instrumenter.getInstrumentedByteCode(stream);
                Class instrumentedClass = (Class) classLoaderMethod.invoke(urlClassLoader, packagename, instrumentedByteCode, 0, instrumentedByteCode.length);
                instrumentedClasses.put(packagename, instrumentedClass);

            }

            InstanceHelper.setClassLoader(urlClassLoader);
            InstanceHelper.init(urlClassLoader, bytecodeLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<MethodNode> getMethodNodes() {
        return methodNodes;
    }

    public static MethodNode getMethodNode(String packagename, String methodName, String methodDesc) {
        MethodNode node = getMethodNodes().stream()
                .filter(m -> m.name.equals(methodName) && m.desc.equals(methodDesc))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot find methodnode for: " + methodName));
        return node;
    }

    private static byte[] getInstrumentedByteCode(InputStream classStream) throws IOException {
        ClassReader classReader = new ClassReader(classStream);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);


        methodNodes.addAll(classNode.methods);
        ((List<MethodNode>) classNode.methods).stream()
                .forEach(m -> instrumentMethod(m));
        ClassWriter classWriter;
        classWriter = new MyClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, urlClassLoader);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }


    private static void instrumentMethod(MethodNode methodNode) {

        if (methodNode.instructions.size() == 0) {
            return;
        }
        String methodInfo = methodNode.name + " " + methodNode.desc;
        InsnList instructions = methodNode.instructions;
        List<AbstractInsnNode> originalInstructions = Arrays.asList(instructions.toArray());
        for (AbstractInsnNode current : originalInstructions) {
            int index = originalInstructions.indexOf(current);
            if (current instanceof LabelNode) {
                instructions.insert(current, createLoggingInjection(current, index));
            } else {
                instructions.insertBefore(current, createLoggingInjection(current, index));
                instructions.insert(current, createLogMethodReturnValue(current, index));
            }
        }
        instructions.insertBefore(instructions.get(0), createMethodBeginInjection(methodInfo));
    }

    private static InsnList createMethodBeginInjection(String methodDesc) {
        InsnList instructionList = new InsnList();
        instructionList.add(new LdcInsnNode(methodDesc));
        instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logBEGINMETHOD", "(Ljava/lang/String;)V"));
        return instructionList;
    }

    public static InsnList createLogMethodReturnValue(AbstractInsnNode current, int methodIdentifierIndex) {
        InsnList instructionList = new InsnList();
        if (current instanceof MethodInsnNode) {
            if (Jutil.methodReturnsInt(((MethodInsnNode) current).desc)) {
                instructionList.add(createLogStackTopInjection(methodIdentifierIndex));
            }
        } else if (current.getOpcode() == Opcodes.GETFIELD) {
            if (Jutil.fieldReturnsInt(((FieldInsnNode) current).desc)) {
                instructionList.add(createLogStackTopInjection(methodIdentifierIndex));
            }
        }
        return instructionList;
    }

    private static InsnList createLogStackTopInjection(int methodIdentifierIndex) {
        InsnList instructionList = new InsnList();
        instructionList.add(new InsnNode(Opcodes.DUP));
        instructionList.add(new IntInsnNode(Opcodes.BIPUSH, methodIdentifierIndex));
        instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logStackTop", "(II)V"));
        return instructionList;
    }

    private static InsnList createLoggingInjection(AbstractInsnNode current, int methodIndentifierIndex) {
        InsnList instructionList = new InsnList();

        if (current instanceof FrameNode) {
            return instructionList;
        }


        if (current.getOpcode() == Opcodes.ILOAD) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logILOAD", "(I)V"));
        } else if (current.getOpcode() == Opcodes.LLOAD) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLLOAD", "(I)V"));
        } else if (current.getOpcode() == Opcodes.ACONST_NULL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logACONST_NULL", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_M1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_M1", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_0) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_0", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_1", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_2) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_2", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_3) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_3", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_4) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_4", "()V"));
        } else if (current.getOpcode() == Opcodes.ICONST_5) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logICONST_5", "()V"));
        } else if (current.getOpcode() == Opcodes.LCONST_0) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLCONST_0", "()V"));
        } else if (current.getOpcode() == Opcodes.LCONST_1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLCONST_1", "()V"));
        } else if (current.getOpcode() == Opcodes.BIPUSH) {
            instructionList.add(new IntInsnNode(Opcodes.BIPUSH, ((IntInsnNode) current).operand));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logBIPUSH", "(I)V"));
        } else if (current.getOpcode() == Opcodes.SIPUSH) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((IntInsnNode) current).operand));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logSIPUSH", "(I)V"));
        } else if (current.getOpcode() == Opcodes.IADD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIADD", "()V"));
        } else if (current.getOpcode() == Opcodes.LADD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLADD", "()V"));
        } else if (current.getOpcode() == Opcodes.ISUB) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logISUB", "()V"));
        } else if (current.getOpcode() == Opcodes.LSUB) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLSUB", "()V"));
        } else if (current.getOpcode() == Opcodes.IDIV) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIDIV", "()V"));
        } else if (current.getOpcode() == Opcodes.IMUL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIMUL", "()V"));
        } else if (current.getOpcode() == Opcodes.IREM) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIREM", "()V"));
        } else if (current.getOpcode() == Opcodes.INEG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logINEG", "()V"));
        } else if (current.getOpcode() == Opcodes.LNEG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLNEG", "()V"));
        } else if (current.getOpcode() == Opcodes.LMUL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLMUL", "()V"));
        } else if (current.getOpcode() == Opcodes.LDIV) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDIV", "()V"));
        } else if (current.getOpcode() == Opcodes.LCMP) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLCMP", "()V"));
        } else if (current.getOpcode() == Opcodes.ISTORE) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logISTORE", "(I)V"));
        } else if (current.getOpcode() == Opcodes.LSTORE) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLSTORE", "(I)V"));
        } else if (current.getOpcode() == Opcodes.IFNE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFNE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFLE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFLE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFGE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFGE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFGT) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFGT", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFLT) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFLT", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPNE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPNE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPGE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPGE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPGT) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPGT", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPLE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPLE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPLT) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPLT", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ICMPEQ) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ICMPEQ", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ACMPNE) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ACMPNE", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IF_ACMPEQ) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIF_ACMPEQ", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.GOTO) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logGOTO", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.RETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logRETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.IRETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIRETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.ARETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logARETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.LRETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLRETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.GETSTATIC) {
            String s = ((FieldInsnNode) current).owner + ((FieldInsnNode) current).name + ((FieldInsnNode) current).desc;
            instructionList.add(new LdcInsnNode(s));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logGETSTATIC", "(Ljava/lang/Object;)V"));
        } else if (current.getOpcode() == Opcodes.LDC) {
            LdcInsnNode lcdNode = (LdcInsnNode) current;
            if (lcdNode.cst instanceof Integer) {
                instructionList.add(new LdcInsnNode(lcdNode.cst));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(I)V"));
            } else if (lcdNode.cst instanceof Long) {
                instructionList.add(new LdcInsnNode(lcdNode.cst));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(J)V"));
            } else if (lcdNode.cst instanceof String) {
                instructionList.add(new LdcInsnNode(lcdNode.cst));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(Ljava/lang/String;)V"));
            } else if (lcdNode.cst instanceof Double) {
                instructionList.add(new LdcInsnNode((((Double) lcdNode.cst).intValue())));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(I)V"));
            } else if (lcdNode.cst instanceof Float) {
                instructionList.add(new LdcInsnNode((((Float) lcdNode.cst).intValue())));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(I)V"));
            } else if (lcdNode.cst instanceof Short) {
                instructionList.add(new LdcInsnNode((((Short) lcdNode.cst).intValue())));
                instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLDC", "(I)V"));
            }
        } else if (current.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            instructionList.add(createLogInvokeMethodInstrumentation((MethodInsnNode) current, "logINVOKEVIRTUAL", methodIndentifierIndex));
        } else if (current.getOpcode() == Opcodes.INVOKESTATIC) {
            instructionList.add(createLogInvokeMethodInstrumentation((MethodInsnNode) current, "logINVOKESTATIC", methodIndentifierIndex));
        } else if (current.getOpcode() == Opcodes.INVOKESPECIAL) {
            instructionList.add(createLogInvokeMethodInstrumentation((MethodInsnNode) current, "logINVOKESPECIAL", methodIndentifierIndex));
        } else if (current.getOpcode() == Opcodes.INVOKEINTERFACE) {
            instructionList.add(createLogInvokeMethodInstrumentation((MethodInsnNode) current, "logINVOKEINTERFACE", methodIndentifierIndex));
        } else if (current.getOpcode() == Opcodes.LOOKUPSWITCH) {
            instructionList.add(createLookupSwitchInstrumentation((LookupSwitchInsnNode) current));
        } else if (current.getOpcode() == Opcodes.TABLESWITCH) {
            instructionList.add(createTableSwitchInstrumentation((TableSwitchInsnNode) current));
        } else if (current instanceof LabelNode) {
            instructionList.add(new LdcInsnNode(((LabelNode) current).getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLabel", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.NEW) {
            String desc = ((TypeInsnNode) current).desc;
            instructionList.add(new LdcInsnNode(desc));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logNEW", "(Ljava/lang/Object;)V"));
        } else if (current.getOpcode() == Opcodes.NEWARRAY) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logNEWARRAY", "()V"));
        } else if (current.getOpcode() == Opcodes.ANEWARRAY) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logANEWARRAY", "()V"));
        } else if (current.getOpcode() == Opcodes.POP) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logPOP", "()V"));
        } else if (current.getOpcode() == Opcodes.POP2) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logPOP2", "()V"));
        } else if (current.getOpcode() == Opcodes.DUP) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logDUP", "()V"));
        } else if (current.getOpcode() == Opcodes.DUP_X1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logDUP_X1", "()V"));
        } else if (current.getOpcode() == Opcodes.AASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logAASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.AALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logAALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.ASTORE) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logASTORE", "(I)V"));
        } else if (current.getOpcode() == Opcodes.ALOAD) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logALOAD", "(I)V"));
        } else if (current.getOpcode() == Opcodes.IALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.IASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.BALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logBALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.CASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logCASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.CALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logCALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.BASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logBASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.IAND) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIAND", "()V"));
        } else if (current.getOpcode() == Opcodes.IOR) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIOR", "()V"));
        } else if (current.getOpcode() == Opcodes.IXOR) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIXOR", "()V"));
        } else if (current.getOpcode() == Opcodes.IUSHR) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIUSHR", "()V"));
        } else if (current.getOpcode() == Opcodes.ISHL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logISHL", "()V"));
        } else if (current.getOpcode() == Opcodes.INSTANCEOF) {
            instructionList.add(new LdcInsnNode(((TypeInsnNode) current).desc));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logINSTANCEOF", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFEQ) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFEQ", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.CHECKCAST) {
            instructionList.add(new LdcInsnNode(((TypeInsnNode) current).desc));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logCHECKCAST", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFNULL) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFNULL", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.IFNONNULL) {
            instructionList.add(new LdcInsnNode(((JumpInsnNode) current).label.getLabel().toString()));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIFNONNULL", "(Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.I2L) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logI2L", "()V"));
        } else if (current.getOpcode() == Opcodes.I2C) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logI2C", "()V"));
        } else if (current.getOpcode() == Opcodes.I2B) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logI2B", "()V"));
        } else if (current.getOpcode() == Opcodes.PUTSTATIC) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logPUTSTATIC", "()V"));
        } else if (current.getOpcode() == Opcodes.GETFIELD) {
            instructionList.add(
                    createLogFieldInstrumentation((FieldInsnNode) current, "logGETFIELD", methodIndentifierIndex));
        } else if (current.getOpcode() == Opcodes.PUTFIELD) {
            String owner = ((FieldInsnNode) current).owner;
            String desc = ((FieldInsnNode) current).desc;
            String name = ((FieldInsnNode) current).name;
            instructionList.add(new LdcInsnNode(owner));
            instructionList.add(new LdcInsnNode(desc));
            instructionList.add(new LdcInsnNode(name));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logPUTFIELD", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"));
        } else if (current.getOpcode() == Opcodes.ARRAYLENGTH) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logARRAYLENGTH", "()V"));
        } else if (current.getOpcode() == Opcodes.ATHROW) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logATHROW", "()V"));
        } else if (current.getOpcode() == Opcodes.IINC) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logIINC", "()V"));
        } else if (current.getOpcode() == Opcodes.LXOR) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLXOR", "()V"));
        } else if (current.getOpcode() == Opcodes.L2I) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logL2I", "()V"));
        } else if (current.getOpcode() == Opcodes.MONITORENTER) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logMONITORENTER", "()V"));
        } else if (current.getOpcode() == Opcodes.MONITOREXIT) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logMONITOREXIT", "()V"));
        } else if (current.getOpcode() == Opcodes.D2F) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "D2F", "()V"));
        } else if (current.getOpcode() == Opcodes.D2I) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "D2I", "()V"));
        } else if (current.getOpcode() == Opcodes.D2L) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "D2L", "()V"));
        } else if (current.getOpcode() == Opcodes.DADD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DADD", "()V"));
        } else if (current.getOpcode() == Opcodes.DALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.DASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.DCMPG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DCMPG", "()V"));
        } else if (current.getOpcode() == Opcodes.DCMPL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DCMPL", "()V"));
        } else if (current.getOpcode() == Opcodes.DCONST_0) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DCONST_0", "()V"));
        } else if (current.getOpcode() == Opcodes.DCONST_1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DCONST_1", "()V"));
        } else if (current.getOpcode() == Opcodes.DDIV) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DDIV", "()V"));
        } else if (current.getOpcode() == Opcodes.DLOAD) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DLOAD", "(I)V"));
        } else if (current.getOpcode() == Opcodes.DMUL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DMUL", "()V"));
        } else if (current.getOpcode() == Opcodes.DNEG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DNEG", "()V"));
        } else if (current.getOpcode() == Opcodes.DREM) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DREM", "()V"));
        } else if (current.getOpcode() == Opcodes.DRETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DRETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.DSTORE) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DSTORE", "(I)V"));
        } else if (current.getOpcode() == Opcodes.DSUB) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "DSUB", "()V"));
        } else if (current.getOpcode() == Opcodes.F2I) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "F2I", "()V"));
        } else if (current.getOpcode() == Opcodes.F2L) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "F2L", "()V"));
        } else if (current.getOpcode() == Opcodes.FADD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FADD", "()V"));
        } else if (current.getOpcode() == Opcodes.FALOAD) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FALOAD", "()V"));
        } else if (current.getOpcode() == Opcodes.FASTORE) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FASTORE", "()V"));
        } else if (current.getOpcode() == Opcodes.FCMPG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FCMPG", "()V"));
        } else if (current.getOpcode() == Opcodes.FCMPL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FCMPL", "()V"));
        } else if (current.getOpcode() == Opcodes.FCONST_0) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FCONST_0", "()V"));
        } else if (current.getOpcode() == Opcodes.FCONST_1) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FCONST_1", "()V"));
        } else if (current.getOpcode() == Opcodes.FCONST_2) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logFCONST_2", "()V"));
        } else if (current.getOpcode() == Opcodes.FDIV) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FDIV", "()V"));
        } else if (current.getOpcode() == Opcodes.FLOAD) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FLOAD", "(I)V"));
        } else if (current.getOpcode() == Opcodes.FMUL) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FMUL", "()V"));
        } else if (current.getOpcode() == Opcodes.FNEG) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FNEG", "()V"));
        } else if (current.getOpcode() == Opcodes.FREM) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FREM", "()V"));
        } else if (current.getOpcode() == Opcodes.FRETURN) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FRETURN", "()V"));
        } else if (current.getOpcode() == Opcodes.FSTORE) {
            instructionList.add(new IntInsnNode(Opcodes.SIPUSH, ((VarInsnNode) current).var));
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FSTORE", "(I)V"));
        } else if (current.getOpcode() == Opcodes.FSUB) {
            instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "log" + "FSUB", "()V"));
        }

        return instructionList;
    }


    private static InsnList createLogFieldInstrumentation(FieldInsnNode current, String loggerMethodName, int methodIndentifierIndex) {
        InsnList instructionList = new InsnList();
        instructionList.add(new LdcInsnNode(current.owner));
        instructionList.add(new LdcInsnNode(current.desc));
        instructionList.add(new LdcInsnNode(current.name));
        instructionList.add(new IntInsnNode(Opcodes.BIPUSH, methodIndentifierIndex));
        instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION,
                loggerMethodName, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V"));
        return instructionList;
    }

    private static InsnList createLogInvokeMethodInstrumentation(MethodInsnNode current,
                                                                 String loggerMethodName, int methodIndentifierIndex) {
        InsnList instructionList = new InsnList();
        String s = current.owner + '|' + current.name + '|' + current.desc;
        instructionList.add(new LdcInsnNode(s));
        instructionList.add(new IntInsnNode(Opcodes.BIPUSH, methodIndentifierIndex));
        instructionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                LOGGER_LOCATION, loggerMethodName, "(Ljava/lang/Object;I)V"));
        return instructionList;
    }

    private static InsnList createTableSwitchInstrumentation(TableSwitchInsnNode current) {
        InsnList insnList = new InsnList();

        insnList.add(new IntInsnNode(Opcodes.SIPUSH, current.min));
        insnList.add(new IntInsnNode(Opcodes.SIPUSH, current.max));

        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logTABLESWITCH", "(II)V"));
        return insnList;
    }


    private static InsnList createLookupSwitchInstrumentation(LookupSwitchInsnNode current) {
        InsnList insnList = new InsnList();
        List<Integer> keys = current.keys;
        List<LabelNode> labels = current.labels;
        LabelNode dflt = current.dflt;

        insnList.add(new IntInsnNode(Opcodes.SIPUSH, keys.size()));
        insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Integer"));
        for (int i = 0; i < keys.size(); i++) {
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new IntInsnNode(Opcodes.SIPUSH, i));
            insnList.add(new LdcInsnNode(keys.get(i)));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
            insnList.add(new InsnNode(Opcodes.AASTORE));
        }
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;"));

        insnList.add(new IntInsnNode(Opcodes.BIPUSH, labels.size()));
        insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));

        for (int i = 0; i < labels.size(); i++) {
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new IntInsnNode(Opcodes.SIPUSH, i));
            insnList.add(new LdcInsnNode(labels.get(i).getLabel().toString()));
            insnList.add(new InsnNode(Opcodes.AASTORE));
        }
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;"));
        insnList.add(new LdcInsnNode(dflt.getLabel().toString()));

        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, LOGGER_LOCATION, "logLOOKUPSWITCH", "(Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V"));
        return insnList;
    }

}
