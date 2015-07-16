package org.tsers.junitquest.solver;


import org.tsers.junitquest.expr.AddNode;
import org.tsers.junitquest.expr.AndNode;
import org.tsers.junitquest.expr.ExprNode;
import org.tsers.junitquest.expr.IntNode;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LogicalSolverTest {

    ExprNode baseAnd = new AndNode(
            Arrays.asList(new IntNode(555), new IntNode(666))
    );

    @Test
    public void testcombineANDs() {

        ExprNode newAndNode = LogicalSolver.combineANDs.apply(baseAnd);

        assertEquals(2, newAndNode.getChildren().size());
    }

    @Test
    public void testcombineANDs2() {

        ExprNode andNode = new AndNode(Arrays.asList(
                new AndNode(
                        Arrays.asList(baseAnd)
                )));
        ExprNode newAndNode = LogicalSolver.combineANDs.apply(andNode);

        assertEquals(2, newAndNode.getChildren().size());
    }

    @Test
    public void testcombineANDs3() {

        ExprNode andNode = new AndNode(Arrays.asList(
                new AndNode(
                        Arrays.asList(
                                new AndNode(
                                        Arrays.asList(new AndNode(Arrays.asList(baseAnd)))
                                ))
                )));

        ExprNode newAndNode = LogicalSolver.combineANDs.apply(andNode);

        assertEquals(2, newAndNode.getChildren().size());
    }

    @Test
    public void testCombineADDS() {

        ExprNode addNode = new AddNode(Arrays.asList(
                new AddNode(Arrays.asList(
                        new AddNode(Arrays.asList(
                                new AddNode(Arrays.asList(
                                        new IntNode(111), new IntNode(222), new IntNode(333), new IntNode(444)
                                ))
                        )
                        )
                ))
        ));

        ExprNode newAddnode = LogicalSolver.reduceAdds.apply(addNode);
        assertEquals(4, newAddnode.getChildren().size());
    }


}
