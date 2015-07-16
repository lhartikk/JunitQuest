package org.tsers.junitquest.testmethods;


import java.util.List;

public class MethodTypes {


    static {
        System.out.println("static block");
    }

    public MethodTypes(int a) {
        if (a == 2) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }


    public MethodTypes(MethodTypes methodTypes, int a) {
        if (a == 2) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }


    public static void callStaticMethod(int a) {
        if (isZero(a)) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void callStaticMethod2(int a, int c) {
        if (someStaticMethod(a, c) == 0) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }

    }

    public static void callStaticMethod3(int a) {
        boolean b = a == 0;
        if (b) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }

    }


    public void callVirtualMethod(int a) {
        if (virtualIsZero(a)) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public void callVirtualMethod2(int a, int b) {
        if (virtualIsZero(a, b)) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public void callPrivateMethod(int a) {
        if (privateMethod() == a) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public void callInterface(int a) {
        MyInterFace mif = new MyClass();

        if (mif.getSomeInteger() == a) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    static boolean isZero(int a) {
        return a == 0;
    }

    static int someStaticMethod(int a, int c) {
        if (a == 14) {
            if (c == 15) {
                return 0;
            }
        }
        return 1;
    }


    boolean virtualIsZero(int a) {
        return a == 0;
    }

    boolean virtualIsZero(int a, int b) {
        return a == 0 || b == 0;
    }

    private int privateMethod() {
        return 1010;
    }

    public static void listTest(List b) {
        System.out.println("branch 1");
    }

    public static void infiniteLoopTest() {
        int b = 123;
        while (b > 0) {
            System.out.println("branch 1");
        }

        System.out.println("branch 2");
    }

}
