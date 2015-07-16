package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class LocalVariablesTest extends JunitQuestTest {


    @Test
    public void localIntTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.LocalVariables", "localIntTest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void staticIntTestTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.LocalVariables", "staticIntTest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void privateLocalIntTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.LocalVariables", "privateLocalIntTest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }
    @Test
    public void privateStaticIntTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.LocalVariables", "privateStaticIntTest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

}
