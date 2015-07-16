package org.tsers.junitquest.solver;


import org.tsers.junitquest.Jutil;
import org.junit.Test;
import org.tsers.junitquest.expr.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JutilTest {


    @Test
    public void findAllNodeTypesTest() {
        ExprNode node =
                new EqualNode(Arrays.asList(
                        new AddNode(
                                Arrays.asList(new LocalNode(1),
                                        new LocalNode(2),
                                        new IntNode(10),
                                        new AddNode(Arrays.asList(
                                                new LocalNode(3), new IntNode(22)))
                                ))));
        List<ExprNode> found = Jutil.findAllNodeTypes(LocalNode.class).apply(node);
        assertEquals(true, found.get(0).equals(new LocalNode(1)));
        assertEquals(true, found.get(1).equals(new LocalNode(2)));
        assertEquals(true, found.get(2).equals(new LocalNode(3)));


    }

    @Test
    public void findAllNodeTypesTest2() {
        ExprNode node =
                new EqualNode(Arrays.asList(
                        new AddNode(
                                Arrays.asList(new LocalNode(1),
                                        new LocalNode(2),
                                        new IntNode(10),
                                        new AddNode(Arrays.asList(
                                                new LocalNode(3), new IntNode(22)))
                                ))));
        List<ExprNode> found = Jutil.findAllNodeTypes(Object.class).apply(node);
        assertEquals(true, found.get(0).equals(new LocalNode(1)));

  }

    @Test
    public void findAllNodeTypesTest3() {
        ExprNode node =
                new GreaterThanNode(Arrays.asList(
                        new AddNode(
                                Arrays.asList(
                                        new IntNode(123),
                                        new AddNode(
                                                Arrays.asList(new LocalNode(1), new LocalNode(2))
                                        )
                                ))));
        List<ExprNode> found = Jutil.findAllNodeTypes(LocalNode.class).apply(node);
        assertEquals(true, found.get(0).equals(new LocalNode(1)));

    }


}
