package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class GeneralInstructionsTest extends JunitQuestTest {

    @Test
    public void testIFNULL() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "IFNULLtest", "(Lorg/tsers/junitquest/testgenerator/MyInteger;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIFNONNULL() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "IFNONNULLtest", "(Lorg/tsers/junitquest/testgenerator/MyInteger;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testACONST_NULL() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "ACONST_NULLtest", "(Lorg/tsers/junitquest/testgenerator/MyInteger;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testTABLESWITCH() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "TABLESWITCHtest", "(I)I");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testINSTANCEOF() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "INSTANCEOFtest", "(Ljava/lang/Object;)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testCHECKCAST() throws Exception {
        List tests = generator.generateTests(
                "org.tsers.junitquest.testmethods.GeneralInstructions",
                "CHECKCASTtest", "(Ljava/lang/Object;I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

}
