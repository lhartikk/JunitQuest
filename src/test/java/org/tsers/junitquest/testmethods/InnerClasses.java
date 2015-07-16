package org.tsers.junitquest.testmethods;

/**
 * Created by lhartikk on 16.7.2015.
 */
public class InnerClasses {

    public class PublicInnerClass {

    }

    private class PrivateInnerClass {

    }

    public static class PublicStaticClass {

    }

    public void someMethod(int a) {
        if (a > 9000) {
            System.out.println("BRANCH 1");
        } else {
            System.out.println("BRANCH 2");
        }
    }

}
