package org.tsers.junitquest.testgenerator;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArrayInstructionsTest extends JunitQuestTest {


    @Test
    public void intArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "intArrayTest", "([II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void longArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "longArrayTest", "([JI)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void doubleArrayTestTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "doubleArrayTest", "([DI)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void floatArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "floatArrayTest", "([FI)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void charArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "charArrayTest", "([CI)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void booleanArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "booleanArrayTest", "([ZI)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void byteArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "byteArrayTest", "([BI)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void objectArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "objectArrayTest", "([Ljava/lang/Object;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void stringArrayTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArrayInstructions", "stringArrayTest", "([Ljava/lang/String;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


}
