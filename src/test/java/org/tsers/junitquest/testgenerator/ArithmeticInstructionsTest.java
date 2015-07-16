package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArithmeticInstructionsTest extends JunitQuestTest {



    @Test
    public void testIMUL() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IMULtest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIDIV() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IDIVtest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testISUB() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "ISUBtest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testISUB2() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "ISUBtest2", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIFGE() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IFGEtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIFGT() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IFGTtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIFLE() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IFLEtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIFLT() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IFLTtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIF_ICMPEQ() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IF_ICMPEQtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIF_ICMPGE() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IF_ICMPGEtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIF_ICMPGT() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IF_ICMPGTtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIF_ICMPLE() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IF_ICMPLEtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testIF_ICMPLT() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "IF_ICMPLTtest", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testLCMP() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "LCMPtest", "(J)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testLCMP2() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "LCMPtest2", "(J)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testLDC() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticInstructions", "LDCtest", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


}
