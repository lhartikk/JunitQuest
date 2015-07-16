package org.tsers.junitquest.testmethods;

public class ArithmeticMethods {


    public static void arithmeticMethod(int a) {
        a = a + 1000;
        if (a == 0) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void arithmeticMethod2(int a) {
        if (a == 0) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void arithmeticMethod3(int a) {
        a = a + 1000;
        a = a + 2000;
        a = a + 3000;
        if (a == 0) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void arithmeticMethod4(int a) {
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == 9999) {
            System.out.println("branch 2");
        } else if (a == 19999) {
            System.out.println("branch 3");
        } else {
            System.out.println("branch 4");
        }
    }

    public static void arithmeticMethod5(int a) {
        a = a + 1000;
        a = a + 2000;
        a = a + 5000;
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == 9999) {
            System.out.println("branch 2");
        } else if (a == 19999) {
            System.out.println("branch 3");
        } else {
            System.out.println("branch 4");
        }
    }


    public static void arithmeticMethod6(int a) {
        if (a == 0) {
            System.out.println("branch 1");
        }
        a = a + 1000;
        if (a == 9999) {
            System.out.println("branch 2");
        }
        a = a + 2000;
        if (a == 19999) {
            System.out.println("branch 3");
        } else {
            System.out.println("branch 4");
        }
    }

    public static void arithmeticMethod7(int a) {
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == 1000) {
            System.out.println("branch 2");
        } else {
            System.out.println("branch 3");
        }
    }

    public static void arithmeticMethod8(int a) {
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == -1) {
            System.out.println("branch 2");
        } else if (a == 1) {
            System.out.println("branch 3");
        } else if (a == 2) {
            System.out.println("branch 4");
        } else if (a == 3) {
            System.out.println("branch 5");
        } else if (a == 4) {
            System.out.println("branch 6");
            ;
        } else if (a == 5) {
            System.out.println("branch 7");
        } else {
            System.out.println("branch 8");
        }
    }

    public static void arithmeticMethod9(int a) {
        int c = 1000 + 2000;
        c = c + 1;
        a = a + c;
        a = a + 1;
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == 5000) {
            System.out.println("branch 2");
        } else {
            System.out.println("branch 3");
        }
    }

    public static void arithmeticMethod10(int a) {
        a = a + 10;
        if (a == 0) {
            System.out.println("branch 1");
        } else if (a == 5000) {
            System.out.println("branch 2");
        } else {
            System.out.println("branch 3");
        }
    }

    public static void arithmeticMethod11(int a) {

        if (a == 0) {
            System.out.println("branch 1");
        } else {
            if (a == 5000) {
                System.out.println("branch 2");
            } else {
                if (a == 1) {
                    System.out.println("branch 3");
                }
            }
        }
    }

    public static void arithmeticMethod12(int a, int b) {
        if (a == 123) {
            System.out.println("branch 1");
        }
        if (b == 321) {
            System.out.println("branch 2");
        }
    }

    public static void arithmeticMethod13(int a, int b, int c) {
        a = a + 100;
        b = b + 100;
        c = c + 100;
        if (a == 123) {
            System.out.println("branch 1");
        }
        if (b == 321) {
            System.out.println("branch 2");
        }
        if (c == 999) {
            System.out.println("branch 3");
        }
    }

    public static void arithmeticMethod14(int a, int b, int c) {

        if (a == 123) {
            System.out.println("branch 1");
        } else if (a == 124 && b != 123 && c != 123) {
            System.out.println("branch 2");
        }

        int d = 1000;

        a = a + 100;
        b = b + 100;
        c = c + 100;

        a = a + d;

        if (a == 1000) {
            if (b == 1000) {
                System.out.println("branch 3");
            }
        }
    }

    public static void arithmeticMethod15(int a) {
        switch (a) {
            case 0:
                System.out.println("branch 1");
                break;
            case 1:
                System.out.println("branch 2");
                break;
            default:
                System.out.println("branch 3");
                break;
        }
    }

    public static void arithmeticMethod16(int a) {
        switch (a) {
            case 0:
                System.out.println("branch 1");
                break;
            case 1:
                System.out.println("branch 2");
                break;
            default:
                System.out.println("branch 3");
                break;
        }

        if (a == 10) {
            System.out.println("branch 4");
        }
    }

    public void intComparisons(int a, int b) {
        if (b == 1) {
            System.out.println("branch 1");
            return;
        }
        if (a == 1) {
            System.out.println("branch 2");
            return;
        }

        if (a == 0 || b == 0) {
            System.out.println("branch 3");
            return;
        }

    }

    public void intComparisons2(String s, int value, int lowerBound, int upperBound) {

        if ((value < lowerBound) || (value > upperBound)) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }

    }

    public void longComparisons(long a, long b) {
        if (b == 1) {
            System.out.println("branch 1");
            return;
        }
        if (a == 1) {
            System.out.println("branch 2");
            return;
        }

        if (a == 0 || b == 0) {
            System.out.println("branch 3");
            return;
        }

        System.out.println("branch 4");

    }

    public void ambiguousVariables(int a, int b, int c) {
        if (a + b + c > 0) {
            System.out.println("branch 1");
        }

        int d = a * b * c;

        if (d > 0) {
            System.out.println("branch 2");
        }
    }

    public void longParameter(long a, int b) {
        if (b == 123) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public void longlonglonglongParameter(long a, long b, long c, long d) {
        if (a == 123) {
            System.out.println("branch 1");
        }
        if (b == 555123) {
            System.out.println("branch 2");
        }
        if (c == 6663) {
            System.out.println("branch 3");
        }
        if (d == 12355) {
            System.out.println("branch 4");
        }
    }

    public void longintlongintParameter(long a, int b, int c, long d) {
        if (a == 123) {
            System.out.println("branch 1");
        }
        if (b == 555123) {
            System.out.println("branch 2");
        }
        if (c == 6663) {
            System.out.println("branch 3");
        }
        if (d == 12355) {
            System.out.println("branch 4");
        }
    }

    public void longMaxValue(long a) {
        if (a == Long.MAX_VALUE) {
            System.out.println("branch 1");
        }
    }

    public void doubleEquation(double a) {
        if (a == 2) {
            System.out.println("branch 1");
        }
    }

    public void doubleEquation2(double a, double b) {
        if (a == 2) {
            System.out.println("branch 1");
            b = b + 9000;
            b = b - 2000;
            b = b + 0;
            b = b + 1;
            b = b + 2;
            b = b + 3;
            if (b == 124) {
                System.out.println("branch 2");
            }
        }
    }

    public void floatEquation(float a) {
        if (a == 1242) {
            System.out.println("branch 1");
        }
    }

    public void floatEquation2(float a, float b) {
        if (a == 2) {
            System.out.println("branch 1");
            b = b + 9000;
            b = b - 2000;
            b = b + 0;
            b = b + 1;
            b = b + 2;
            b = b + 3;
            if (b == 124) {
                System.out.println("branch 2");
            }
        }
    }

    public void shortEquation(short a) {
        if (a == 2) {
            System.out.println("branch 1");
        }
        System.out.println("branch 2");
    }


}
