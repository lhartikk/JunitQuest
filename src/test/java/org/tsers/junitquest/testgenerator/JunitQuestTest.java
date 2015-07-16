package org.tsers.junitquest.testgenerator;

import org.tsers.junitquest.Instrumenter;
import org.tsers.junitquest.TestGenerator;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;


public abstract class JunitQuestTest {

    static TestGenerator generator;

    //Example: "/home/lhartikk/programming/junitquest/target/test-classes"
    private static String byteCodeFolder = "SET ME TO RUN TESTS";


    @Before
    public void init() throws Exception {

        if (generator == null) {
            String bytecodeLocation = byteCodeFolder;
            List<String> classes =
                    Arrays.asList(
                            "org.tsers.junitquest.testmethods.ArithmeticMethods",
                            "org.tsers.junitquest.testmethods.ArithmeticInstructions",
                            "org.tsers.junitquest.testmethods.ConstantMethods",
                            "org.tsers.junitquest.testmethods.GeneralInstructions",
                            "org.tsers.junitquest.testmethods.MethodTypes",
                            "org.tsers.junitquest.testmethods.LocalVariables",
                            "org.tsers.junitquest.testmethods.ArrayInstructions",
                            "org.tsers.junitquest.testmethods.InnerClasses",
                            "org.tsers.junitquest.testmethods.MyClass2"

                    );
            Instrumenter.instrumentClasses(classes, bytecodeLocation);
            generator = new TestGenerator();

        }


    }

}
