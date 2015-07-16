package org.tsers.junitquest;


import org.tsers.junitquest.expr.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jutil {

    public static int LABEL_OPCODE = -1;
    public static int BEGIN_METHOD_OPCODE = -2;

    public static BiFunction<Class, List<ExprNode>, ExprNode> createNode = (nodeClass, children) -> {
        try {
            Constructor<? extends ExprNode> constructor = nodeClass.getConstructor(List.class);
            return constructor.newInstance(children);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find List constructor: " + nodeClass);
        }

    };

    //if children cannot be mapped returns the parent itself
    public static BiFunction<ExprNode, Function<ExprNode, ExprNode>, ExprNode> mapChildren = (parent, f) -> {
        try {
            List<ExprNode> mappedChildren =
                    parent.getChildren().stream()
                            .map(f)
                            .collect(Collectors.toList());

            return Jutil.createNode.apply(parent.getClass(), mappedChildren);
        } catch (Exception e) {
            return parent;
        }
    };


    public static BiFunction<ExprNode, Function<ExprNode, ExprNode>, ExprNode> mapFirstChild = (node, function) -> {
        int numOfItemsToMap = 1;
        List<ExprNode> mapped =
                node.getChildren().stream()
                        .limit(numOfItemsToMap)
                        .map(function)
                        .collect(Collectors.toList());

        List<ExprNode> rest =
                node.getChildren().stream()
                        .skip(numOfItemsToMap)
                        .collect(Collectors.toList());
        return Jutil.createNode.apply(node.getClass(), Jutil.combineLists(mapped, rest));
    };

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static Object[] listToArray(List list) {
        return list.stream().toArray(Object[]::new);
    }

    public static <T> List<T> arrayToList(T[] objects) {
        return Arrays.asList(objects);
    }

    public static Object[] shiftLeft(Object[] object) {
        if (object.length == 0) {
            return new Object[0];
        }

        Object ret[] = new Object[object.length - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = object[i + 1];
        }
        return ret;

    }

    public static <T> List<T> combineLists(List<T> a, List<T> b) {
        return Stream.concat(a.stream(), b.stream())
                .collect(Collectors.toList());

    }

    public static <T> List<T> combineLists(List<T> a, List<T> b, List<T> c) {
        return combineLists(combineLists(a, b), c);
    }

    public static boolean containsClazz(ExprNode node, Class clazz) {
        return calcClazzHeight(node, clazz) > 0;
    }

    private static int calcClazzHeight(ExprNode node, Class clazz) {
        return calcClazzHeightRec(node, 0, clazz);
    }

    private static int calcClazzHeightRec(ExprNode node, int i, Class clazz) {
        int largest = 0;
        for (ExprNode c : node.getChildren()) {
            int a = calcClazzHeightRec(c, 0, clazz);
            if (a > largest) {
                largest = a;
            }
        }
        if (clazz.isAssignableFrom(node.getClass())) {
            i++;
        }
        return i + largest;
    }

    //does not apply children if the node itself is transformed
    public static Function<ExprNode, ExprNode> applyRecursively(Function<ExprNode, ExprNode> function) {
        return node -> {
            ExprNode appliedParent = function.apply(node);
            if (!appliedParent.equals(node)) {
                return appliedParent;
            }
            List<ExprNode> appliedChildren = node.getChildren().stream()
                    .map(applyRecursively(function))
                    .filter(n -> node != null)
                    .collect(Collectors.toList());

            return appliedParent.copy(appliedChildren);
        };
    }

    public static Function<ExprNode, List<ExprNode>> findAllNodeTypes(Class clazz) {
        return node -> {

            List<ExprNode> foundChildren = node.getChildren().stream()
                    .map(findAllNodeTypes(clazz))
                    .flatMap(n -> n.stream())
                    .collect(Collectors.toList());
            if (clazz.isAssignableFrom(node.getClass())) {
                return Jutil.combineLists(foundChildren, Arrays.asList(node));
            } else {
                return foundChildren;
            }

        };

    }

    public static boolean isPrimitive(Class clazz) {
        return (clazz == Integer.TYPE || clazz == Double.TYPE
                || clazz == Boolean.TYPE || clazz == Byte.TYPE ||
                clazz == Short.TYPE || clazz == Long.TYPE ||
                clazz == Float.TYPE || clazz == Character.TYPE);
    }

    public static Class[] descToParameterTypes(String desc) {
        Pattern pattern = Pattern.compile("\\((.*)\\)");

        Matcher m = pattern.matcher(desc);
        m.find();
        String paramsString = m.group(1);

        List<String> params = splitDescParams(paramsString);
        List<Class> classParams = params.stream()
                .map(p -> descParamToClass(p))
                .collect(Collectors.toList());
        return classParams.toArray(new Class[classParams.size()]);
    }

    private static Class descParamToClass(String descParam) {

        int numberOfArrays = 0;
        if (descParam.substring(0, 1).equals("[")) {
            descParam = descParam.substring(1);
            numberOfArrays++;
        }

        if (descParam.substring(0, 1).equals("[")) {
            throw new RuntimeException("2 dimension arrays not supported");
        }

        Class clazz = plainDescparamsToClass(descParam);
        while (numberOfArrays > 0) {
            clazz = Array.newInstance(clazz, 1).getClass();
            numberOfArrays--;
        }
        return clazz;
    }


    private static Class plainDescparamsToClass(String descParam) {
        if (descParam.length() == 1) {
            return primitiveDescToClass(descParam);
        }

        String s = descParam.substring(1);
        String s2 = s.substring(0, s.length() - 1);
        String s3 = s2.replaceAll("/", ".");

        return Instrumenter.getClass(s3);
    }

    public static Class primitiveDescToClass(String descParam) {
        if (descParam.equals("I")) {
            return Integer.TYPE;
        } else if (descParam.equals("Z")) {
            return Boolean.TYPE;
        } else if (descParam.equals("J")) {
            return Long.TYPE;
        } else if (descParam.equals("C")) {
            return Character.TYPE;
        } else if (descParam.equals("D")) {
            return Double.TYPE;
        } else if (descParam.equals("F")) {
            return Float.TYPE;
        } else if (descParam.equals("S")) {
            return Short.TYPE;
        } else if (descParam.equals("B")) {
            return Byte.TYPE;
        }
        throw new RuntimeException("Not a primitive description: " + descParam);
    }

    private static List<String> splitDescParams(String descParams) {
        List<String> matchedParams = new ArrayList<>();
        Pattern paramPattern = Pattern.compile("(\\[*?L.*?;)|(\\[*?I|\\[?Z|\\[?B|\\[*?D|\\[*?F|\\[*?J|\\[*?S|\\[*?C)");
        while (true) {
            Matcher m2 = paramPattern.matcher(descParams);

            if (!m2.find()) {
                break;
            }
            String param = m2.group();
            matchedParams.add(param);
            descParams = descParams.substring(param.length());
        }
        return matchedParams;
    }


    public static Function<ExprNode, ExprNode> curry(BiFunction<ExprNode, String, ExprNode> f, String o) {
        return a -> f.apply(a, o);
    }

    public static Function<ExprNode, ExprNode> curry(BiFunction<ExprNode, ExprNode, ExprNode> f, ExprNode o) {
        return a -> f.apply(a, o);
    }

    //equation with stacknode(0) on the other side and constant node on the other side
    public static boolean isSimpleEquation(ExprNode node) {
        if (!(node instanceof EqualNode || node instanceof NotEqualNode ||
                node instanceof GreaterThanNode || node instanceof GreaterThanEqNode
                || node instanceof LessThanNode || node instanceof LessThanEqNode)) {
            return false;
        }
        if (node.getChildren().size() != 2) {
            return false;
        }
        if (!node.getChildren().stream().anyMatch(n -> n instanceof StackNode && ((StackNode) n).getValue() == 0)) {
            return false;
        }
        if (!node.getChildren().stream().anyMatch(n -> n instanceof ConstantNode)) {
            return false;
        }
        return true;
    }

    public static boolean methodReturnsInt(String methodDesc) {
        return methodDesc.endsWith(")I");
    }

    public static boolean fieldReturnsInt(String desc) {
        return desc.equals("I");
    }

    public static boolean isClassArray(Class obj) {
        return obj != null && obj.isArray();
    }

    public static Class[] getParameterTypes(AccessibleObject ao) {
        if (ao instanceof Method) {
            return ((Method) ao).getParameterTypes();
        } else if (ao instanceof Constructor) {
            return ((Constructor) ao).getParameterTypes();
        }
        throw new RuntimeException("Cannot get parameter types for: " + ao);
    }

    public static boolean isStatic(AccessibleObject ao) {
        if (ao instanceof Method) {
            return Jutil.isStatic((Method) ao);
        }
        return false;
    }

    public static Class getDeclaringClass(AccessibleObject ao) {
        if (ao instanceof Method) {
            return ((Method) ao).getDeclaringClass();
        } else if (ao instanceof Constructor) {
            return ((Constructor) ao).getDeclaringClass();
        }
        throw new RuntimeException("Cannot get declaring class for: " + ao);
    }

    public static AccessibleObject getAccessibleObject(Class clazz, String methodName, String methodDesc) {
        Class paramTypes[] = Jutil.descToParameterTypes(methodDesc);
        try {
            if (methodName.equals("<init>")) {
                return clazz.getConstructor(paramTypes);
            } else {
                return clazz.getMethod(methodName, paramTypes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find accessible object :" + methodName + " " + methodDesc);
        }
    }

    public static String getJavaName(String fullName) {
        String[] splitted = fullName.split(" ");
        return splitted[splitted.length - 1];
    }

    public static String getClassNameFromPackage(String fullName) {
        String[] splitted = fullName.split("\\.");
        return splitted[splitted.length - 1];
    }

}
