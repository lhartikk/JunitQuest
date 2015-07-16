package org.tsers.junitquest.testgenerator;

import org.tsers.junitquest.CallParam;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArithmeticMethodsTest extends JunitQuestTest {

    @Test
    public void testArithmeticMethod() throws Exception {
        List<List<CallParam>> tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void testArithmeticMethod2() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod2", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod3() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod3", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod4() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod4", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod5() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod5", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod6() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod6", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod7() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod7", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod8() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod8", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod9() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod9", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod11() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod11", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void testArithmeticMethod12() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod12", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void testArithmeticMethod13() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod13", "(III)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void testArithmeticMethod14() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod14", "(III)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void testArithmeticMethod15() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod15", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void testArithmeticMethod16() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "arithmeticMethod16", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void intComparisonsTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "intComparisons", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void intComparisonsTest2() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "intComparisons2", "(Ljava/lang/String;III)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void longComparisonsTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "longComparisons", "(JJ)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void ambiguousVariablesTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "ambiguousVariables", "(III)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void longParameterTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "longParameter", "(JI)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void longlonglonglongParameterTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "longlonglonglongParameter", "(JJJJ)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void longintlongintParameterTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "longintlongintParameter", "(JIIJ)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void doubleEquationTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "doubleEquation", "(D)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void doubleEquation2Test() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "doubleEquation2", "(DD)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void floatEquationTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "floatEquation", "(F)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void floatEquation2Test() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "floatEquation2", "(FF)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void shortEquationTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.ArithmeticMethods", "shortEquation", "(S)V");
        assertTrue(generator.getBc().isAllVisited());
    }


}
