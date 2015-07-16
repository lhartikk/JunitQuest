package org.tsers.junitquest.solver;

import org.tsers.junitquest.Jutil;
import org.tsers.junitquest.expr.AddNode;
import org.tsers.junitquest.expr.AndNode;
import org.tsers.junitquest.expr.ExprNode;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogicalSolver {


    public static Function<ExprNode, ExprNode> reduceAdds = addNode -> {
        if (addNode instanceof AddNode && addNode.getChildren().size() == 1) {
            return combineNodes(addNode.getChildren().get(0), AddNode.class);
        }
        return combineNodes(addNode, AddNode.class);
    };

    public static Function<ExprNode, ExprNode> combineANDs = andNode -> {
        return combineNodes(andNode, AndNode.class);
    };

    public static ExprNode combineNodes(ExprNode node, Class nodeClazz) {
        if (!(node.getClass().isAssignableFrom(nodeClazz))) {
            return node;
        }

        List<ExprNode> combinedNodes = node.getChildren().stream()
                .filter(n -> n.getClass().isAssignableFrom(nodeClazz))
                .map(n -> n.getChildren())
                .flatMap(n -> n.stream())
                .collect(Collectors.toList());

        List<ExprNode> otherNodes = node.getChildren().stream()
                .filter(n -> !(n.getClass().isAssignableFrom(nodeClazz)))
                .collect(Collectors.toList());

        List<ExprNode> newChildren = Jutil.combineLists(combinedNodes, otherNodes);


        ExprNode returnNode = null;
        try {
            Constructor<ExprNode> c = nodeClazz.getConstructor(List.class);
            returnNode = c.newInstance(newChildren);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find constructor: " + nodeClazz);
        }

        if (returnNode.getChildren().stream().anyMatch(n -> n.getClass().isAssignableFrom(nodeClazz))) {
            ExprNode newReturnNode = combineNodes(returnNode, nodeClazz);
            return newReturnNode;
        }

        return returnNode;

    }

}
