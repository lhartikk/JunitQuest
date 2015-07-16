package org.tsers.junitquest.testmethods;


public class LocalVariables {

    int localInt = 1234;
    static int staticInt = 1111;
    private int privateLocalInt = 4444;
    private int privateStaticInt = 535324;

    Integer localInteger = 99999;
    static Integer staticInteger = 88888;


    public int getLocalInt() {
        return localInt;
    }

    public static int getStaticInt() {
        return staticInt;
    }

    public Integer getLocalInteger() {
        return localInteger;
    }

    public void localIntTest(int a) {
        if (a == getLocalInt()) {
            System.out.println("branch 1");
        }
    }

    public void staticIntTest(int a) {
        if (a == getStaticInt()) {
            System.out.println("branch 1");
        }
    }

    public void privateLocalIntTest(int a) {
        if (a == privateLocalInt) {
            System.out.println("branch 1");
        }
    }

    public void privateStaticIntTest(int a) {
        if (a == privateStaticInt) {
            System.out.println("branch 1");
        }
    }


}
