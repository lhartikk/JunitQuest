package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ConstantTest extends JunitQuestTest {

    @Test
    public void booleanTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "booleanTest", "(Z)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void booleanTest2() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "booleanTest", "(ZZ)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void booleanNullTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "stringTest", "(Ljava/lang/String;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void charTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "charTest", "(CC)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void doubleTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "doubleTest", "(DD)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void floatTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "floatTest", "(FF)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void longTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "longTest", "(JJ)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void shortTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "shortTest", "(SS)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void byteTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ConstantMethods", "byteTest", "(BB)V");
        assertTrue(generator.getBc().isAllVisited());
    }

}
