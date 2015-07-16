package org.tsers.junitquest.testmethods;


public class MyClass2 {

    public MyClass2(String s, boolean b, long l) {

    }


    public void method(int a) {
        if (a > 9000) {
            System.out.println("BRANCH 1");
        } else {
            System.out.println("BRANCH 2");
        }
    }
}
