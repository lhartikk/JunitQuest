package org.tsers.junitquest.testmethods;

public class ConstantMethods {


    public static void booleanTest(boolean a) {
        if (a) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }


    public static void booleanTest(boolean a, boolean b) {
        if (a) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }


    public static void stringTest(String s, int a) {
        if (a == 222) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void charTest(char a, char b) {
        char c = a;
        char d = b;
    }

    public static void doubleTest(double a, double b) {
        double c = a;
        double d = b;
    }

    public static void floatTest(float a, float b) {
        float c = a;
        float d = b;
    }

    public static void longTest(long a, long b) {
        long c = a;
        long d = b;
    }

    public static void shortTest(short a, short b) {
        short c = a;
        short d = b;
    }

    public static void byteTest(byte a, byte b) {
        byte c = a;
        byte d = b;

    }


}
