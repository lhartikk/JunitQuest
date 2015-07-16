package org.tsers.junitquest.testgenerator;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class InnerClassesTest extends JunitQuestTest {

    @Test
    public void someMethodTest2() throws Exception {

        List tests = generator.generateTests("org.tsers.junitquest.testmethods.InnerClasses",
                "someMethod", "(I)V");
        assertTrue(generator.getBc().isAllVisited());
    }

}
