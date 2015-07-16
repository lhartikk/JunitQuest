package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MethodTypesTest extends JunitQuestTest {


    @Test
    public void constructorTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "<init>", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void constructorTest2() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MyClass2", "method", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void callStaticMethodTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callStaticMethod", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void callStaticMethodTest2() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callStaticMethod2", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void callStaticMethodTest3() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callStaticMethod3", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void callVirtualMethodTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callVirtualMethod", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void callVirtualMethod2Test() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callVirtualMethod2", "(II)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void callInterfaceTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "callInterface", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }


    @Test
    public void listTest() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "listTest", "(Ljava/util/List;)V");
        assertTrue(generator.getBc().isAllVisited());
    }

    @Test
    public void infiniteLoopTest() throws Exception {
        List tests = generator.generateTests("org.tsers.junitquest.testmethods.MethodTypes", "infiniteLoopTest", "()V");
    }


}
