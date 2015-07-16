package org.tsers.junitquest.solver;

import org.tsers.junitquest.CallParam;
import org.tsers.junitquest.Jutil;
import org.tsers.junitquest.expr.EqualNode;
import org.tsers.junitquest.expr.ExprNode;
import org.tsers.junitquest.expr.LocalNode;
import org.tsers.junitquest.expr.NullNode;
import org.tsers.junitquest.instance.NullInstance;

import java.util.function.Function;

public class NullEquationSolver {


    public static Function<ExprNode, CallParam> nullEquationToCallParam = node -> {

        LocalNode localNode =
                (LocalNode) Jutil.findAllNodeTypes(LocalNode.class).apply(node)
                        .stream().findFirst().get();
        return new CallParam(new NullInstance(), localNode.getValue());

    };

    public static boolean isNullEquation(ExprNode node) {
        return (node instanceof EqualNode) && Jutil.containsClazz(node, NullNode.class);
    }

}
