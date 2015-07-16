package org.tsers.junitquest;

import org.tsers.junitquest.instance.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class TestCaseGenerator {


    private static int variableCounter = 0;

    private static final String INDENT_1 = "  ";
    private static final String INDENT_2 = "    ";

    private static HashMap<String, Integer> methodCounter = new HashMap<>();


    private static void resetVariableCounter() {
        variableCounter = 0;
    }

    private static void resetMethodCounter() {
        methodCounter = new HashMap<>();
    }

    private static int nextVariable() {
        variableCounter++;
        return variableCounter;
    }

    private static int nextMethod(String methodName) {
        if (methodCounter.containsKey(methodName)) {
            int n = methodCounter.get(methodName);
            n++;
            methodCounter.put(methodName, n);
            return n;
        } else {
            int n = 0;
            methodCounter.put(methodName, n);
            return n;
        }
    }

    public static String wrapToTestClass(String methods, String className) {
        resetMethodCounter();
        return
                "import org.junit.Test;\n" +
                        "public class " + className + "Test" + " {\n\n" + methods + "\n}";
    }

    public static String toTestCase(List<List<CallParam>> callParams, Class clazz, String methodName, String methodDesc) throws NoSuchMethodException {
        AccessibleObject method = Jutil.getAccessibleObject(clazz, methodName, methodDesc);
        String s =
                callParams.stream()
                        .map(cp -> createRootCallParam(method, clazz, cp))
                        .map(cp -> callParamToJavaCode(cp, "mainVar"))
                        .map(c -> wrapToTestCase(
                                getMethodNameForTestCase(method) + "Test" + nextMethod(methodName), c))
                        .reduce("", (a, b) -> a + b);
       return s;
    }

    private static String getMethodNameForTestCase(AccessibleObject ao) {
        if (ao instanceof Method) {
            return ((Method) ao).getName();
        } else if (ao instanceof Constructor) {
            return "constructor";
        }
        throw new RuntimeException();
    }

    private static CallParam createRootCallParam(AccessibleObject method, Class className, List<CallParam> cps) {
        if (method instanceof Method) {
            return new CallParam(
                    new ObjectInstance(
                            (Method) method, className,
                            cps.stream().map(cp -> cp.getInstance()).collect(Collectors.toList()))
                    , 0);
        } else if (method instanceof Constructor) {
            return new CallParam(
                    new ObjectInstance(
                            (Constructor) method,
                            cps.stream().skip(1).map(cp -> cp.getInstance()).collect(Collectors.toList()))
                    , 0);
        }
        throw new RuntimeException("Cannot create root call parameter: " + method + " " + className);
    }

    private static String wrapToTestCase(String testName, String javaCode) {
        String a = INDENT_1 + "@Test\n"
                + INDENT_1 + "public void " + testName + "() throws Exception {";
        String b = "\n" + INDENT_1 + "}\n\n";
        return a + javaCode + b;
    }


    private static String primitiveInstanceToVariableType(PrimitiveInstance instance) {
        if (instance.getInstance() instanceof Integer) {
            return "int";
        } else if (instance.getInstance() instanceof Boolean) {
            return "boolean";
        } else if (instance.getInstance() instanceof Long) {
            return "long";
        } else if (instance.getInstance() instanceof Character) {
            return "char";
        } else if (instance.getInstance() instanceof Double) {
            return "double";
        } else if (instance.getInstance() instanceof Short) {
            return "short";
        } else if (instance.getInstance() instanceof Float) {
            return "float";
        } else if (instance.getInstance() instanceof Byte) {
            return "byte";
        }
        throw new RuntimeException("Unknown primitive instance");
    }

    private static String getClassName(Instance instance) {
        if (instance instanceof PrimitiveInstance) {
            return primitiveInstanceToVariableType((PrimitiveInstance) instance);
        } else if (instance instanceof NullInstance) {
            return "Object";
        } else {
            if (((ObjectInstance) instance).getMethod() instanceof Method) {
                Method m = (Method) ((ObjectInstance) instance).getMethod();
                if (Jutil.isStatic(m)) {
                    return handleArraysInDeclarationName(m.getReturnType().getName());
                } else {
                    return handleArraysInDeclarationName(m.getDeclaringClass().getName());
                }

            } else {
                return handleArraysInDeclarationName(((Constructor)
                        ((ObjectInstance) instance).getMethod()).getDeclaringClass().getName());
            }
        }
    }

    private static String handleArraysInDeclarationName(String decl) {

        if (decl.startsWith("[L")) {
            decl = decl.substring(2);
            decl = decl.substring(0, decl.length() - 1);
            decl = decl + "[]";

        } else if (decl.startsWith("[")) {
            decl = decl.substring(1);
            decl = decl.substring(0, decl.length() - 1);
            decl = decl + "[]";
        }
        return decl;
    }

    private static String getOnlyMethodName(ObjectInstance instance) {
        Method m = (Method) instance.getMethod();
        return m.getName();
    }

    private static String getMethodName(ObjectInstance instance) {
        Method m = (Method) instance.getMethod();
        return m.getDeclaringClass().getName() + "." + m.getName();
    }

    public static String callParamToJavaCode(CallParam cp, String varName) {
        resetVariableCounter();
         return getParamNameAndCode(cp.getInstance(), Object.class).getValue();
    }

    private static boolean isMethodCall(Instance instance) {
        if (instance instanceof ObjectInstance) {
            return ((ObjectInstance) instance).getMethod() instanceof Method;
        }
        return false;
    }

    private static boolean isVirtualMethodCall(Instance instance) {
        if (instance instanceof ObjectInstance) {
            ObjectInstance oi = (ObjectInstance) instance;
            if (oi.getMethod() instanceof Method) {
                Method m = (Method) oi.getMethod();
                return !Jutil.isStatic(m);
            }
        }
        return false;
    }

    private static boolean isStaticMethodCall(Instance instance) {
        if (instance instanceof ObjectInstance) {
            ObjectInstance oi = (ObjectInstance) instance;
            if (oi.getMethod() instanceof Method) {
                Method m = (Method) oi.getMethod();
                return Jutil.isStatic(m);
            }
        }
        return false;
    }

    private static boolean isConstructor(Instance instance) {
        if (instance instanceof ObjectInstance) {
            ObjectInstance oi = (ObjectInstance) instance;
            if (oi.getMethod() instanceof Constructor) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVoidMethodCall(Instance instance) {
        if (instance instanceof ObjectInstance) {
            ObjectInstance oi = (ObjectInstance) instance;
            if (oi.getMethod() instanceof Method) {
                Method m = (Method) oi.getMethod();
                return m.getReturnType().equals(Void.TYPE);
            }
        }
        return false;
    }

    private static String getPostfix(Instance instance) {
        if (isMethodCall(instance)) {
            return getMethodName((ObjectInstance) instance);
        } else {
            return getClassName(instance);
        }
    }


    private static Map.Entry<String, String> getParamNameAndCode(Instance param, Class paramType) {
        String variableName = "var" + nextVariable();
        if (param instanceof NullInstance) {
            String s = Jutil.getJavaName(paramType.getName()) + " " + variableName + " = null;";
            return new AbstractMap.SimpleEntry<>(variableName, s);
        } else if (param instanceof PrimitiveInstance) {
            String s = getClassName(param) + " " + variableName + " = " + param.asString() + ";";
            return new AbstractMap.SimpleEntry<>(variableName, s);
        } else if (param instanceof ClassInstance) {
            return new AbstractMap.SimpleEntry<>(variableName, "Class " + variableName + " = " + param.asString() + ";");
        } else if (param instanceof ObjectInstance) {
            return new AbstractMap.SimpleEntry<>
                    (variableName, (createFromObjectInstance(((ObjectInstance) param), variableName)));
        }
        throw new RuntimeException("what to do?");
    }

    private static String createFromObjectInstance(ObjectInstance instance, String var) {
        List<Map.Entry<String, String>> params = getParamEntrys(instance);
        String codeTocreateCallVars =
                params.stream()
                        .map(e -> e.getValue())
                        .reduce(INDENT_2, (a, b) -> a + "\n" + INDENT_2 + b);

        String callVars = "(" +  params.stream()
                .map(e -> e.getKey())
                .collect(Collectors.joining(", ")) + ")";

        if (isVirtualMethodCall(instance)) {
            Map.Entry<String, String> virtualInstance =
                    getParamNameAndCode(instance.getParameters().get(0), Object.class);
            return codeTocreateCallVars
                    + INDENT_2 + virtualInstance.getValue() + "\n"
                    + INDENT_2 + virtualInstance.getKey() + "." + getOnlyMethodName(instance) + callVars + ";";
        } else if (isArrayCall(instance)) {
            String varType = Jutil.getJavaName(instance.getParameters().get(0).build().toString());
            return codeTocreateCallVars +
                    varType + "[] " + var + " = "
                    + "(" + varType + "[]" + ") "
                    + getMethodName(instance) + callVars + ";";
        } else if (isVoidMethodCall(instance)) {
            return codeTocreateCallVars + "\n" + INDENT_2 +
                    getPostfix(instance) + callVars + ";";
        } else if (isConstructor(instance)) {
            return codeTocreateCallVars + "\n" + INDENT_2 +
                    getClassName(instance) + " " + var +
                    " = new " + getClassName(instance) + callVars + ";";
        } else if (isStaticMethodCall(instance)) {
            return codeTocreateCallVars + "\n" + INDENT_2 +
                    getClassName(instance) + " " + var +
                    " = " + getMethodName(instance) + callVars + ";";
        } else {
            throw new RuntimeException("Cannot create code for object");
        }
    }

    private static List<Map.Entry<String, String>> getParamEntrys(ObjectInstance instance) {
        int virtualMethodPrefix = 0;
        if (isVirtualMethodCall(instance)) {
            virtualMethodPrefix++;
        }

        List<Map.Entry<String, String>> params = new ArrayList<>();
        for (int i = virtualMethodPrefix; i < instance.getParameters().size(); i++) {

            Class type = Jutil.getParameterTypes(instance.getMethod())[i - virtualMethodPrefix];
            Instance param = instance.getParameters().get(i);
            params.add(getParamNameAndCode(param, type));
        }
        return params;
    }

    private static boolean isArrayCall(Instance instance) {
        if (instance instanceof ObjectInstance) {
            AccessibleObject ao = ((ObjectInstance) instance).getMethod();
            if (ao instanceof Method) {
                if (((Method) ao).getDeclaringClass().getName().equals("java.lang.reflect.Array")) {
                    return true;
                }
            }
        }
        return false;
    }

}
