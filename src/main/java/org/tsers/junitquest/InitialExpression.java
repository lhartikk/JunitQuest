package org.tsers.junitquest;

import org.tsers.junitquest.expr.*;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitialExpression {

    private static List<CallParam> initialCallparams;


    private static boolean is32bitPrimite(String s) {
        return Arrays.asList("int", "char", "float", "short", "byte", "boolean").contains(s);
    }

    private static boolean is64bitPrimitive(String s) {
        return Arrays.asList("long", "double").contains(s);
    }

    private static boolean isInterface(String s) {
        return s.split(" ")[0].equals("interface");
    }

    private static boolean isClass(String s) {
        return s.split(" ")[0].equals("class");
    }

    public static ExprNode createInitialExpression(AccessibleObject method) {

        Object paramTypes[] = Jutil.getParameterTypes(method);
        List<ExprNode> equalsExpressions = new ArrayList<>();

        int prefixForVirtualCalls = getPrefix(method);
        if (prefixForVirtualCalls == 1) {
            ExprNode virtualMethodInstanceExpr =
                    createInstanceOfExpression(0, Jutil.getDeclaringClass(method).getName());
            equalsExpressions.add(virtualMethodInstanceExpr);
        }
        int prefixFor64BitPrimitives = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            int localIndex = i + prefixForVirtualCalls + prefixFor64BitPrimitives;
            String paramType = paramTypes[i].toString();
            if (is32bitPrimite(paramType)) {
                equalsExpressions.add(createIntNodeExpression(localIndex));
            } else if (is64bitPrimitive(paramType)) {
                equalsExpressions.add(createIntNodeExpression(localIndex));
                prefixFor64BitPrimitives++;
            } else if (isInterface(paramType) || isClass(paramType)) {
                String instanceName = paramType.split(" ")[1];
                equalsExpressions.add(createInstanceOfExpression(localIndex, instanceName));
            } else {
                throw new RuntimeException("Cannot create initial expression, unknown type:" + paramType);
            }

        }

        ExprNode initialExpresssion = new AndNode(equalsExpressions);
        initialCallparams = Solver.solve(initialExpresssion).get(0);
        return initialExpresssion;

    }

    private static ExprNode createInstanceOfExpression(int localIndex, String instanceName) {
        ExprNode local = new LocalNode(localIndex);
        ExprNode instanceOfNode = new InstanceOfNode(local, instanceName);
        return new NotEqualNode(Arrays.asList(new IntNode(0), instanceOfNode));
    }

    private static ExprNode createIntNodeExpression(int localIndex) {
        ExprNode local = new LocalNode(localIndex);
        ExprNode constant = new IntNode(0);
        return new EqualNode(Arrays.asList(local, constant));
    }

    private static int getPrefix(AccessibleObject method) {
        if (Jutil.isStatic(method)) {
            return 0;
        } else return 1;
    }

    public static List<CallParam> getInitialCallParams() {
        return initialCallparams;

    }


}
