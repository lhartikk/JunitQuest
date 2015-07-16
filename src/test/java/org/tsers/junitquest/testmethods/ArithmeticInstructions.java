package org.tsers.junitquest.testmethods;

public class ArithmeticInstructions {


    public static void IMULtest(int a) {
        a = a * 3;
        if (a == 9999) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void IDIVtest(int a) {
        a = a / 3;
        if (a == 9999) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void ISUBtest(int a) {
        a = a - 3333;
        if (a == 0) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void ISUBtest2(int a) {
        a = a - 3333;
        if (a == 7777) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }

    public static void IFGEtest(int a, int b) {
        if (a < 0) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
            System.out.println("branch 4");
        }
    }

    public static void IFGTtest(int a, int b) {
        if (a <= 0) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
            System.out.println("branch 4");
        }
    }

    public static void IFLEtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a > 0) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
            System.out.println("branch 4");
        }
    }

    public static void IFLTtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a >= 0) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
            System.out.println("branch 4");
        }
    }

    public static void IF_ICMPEQtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a != 999) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
        }
    }

    public static void IF_ICMPNEtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a == 999) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
        }
    }

    public static void IF_ICMPGEtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a < 999) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
        }
    }

    public static void IF_ICMPGTtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a <= 999) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
            System.out.println("branch 4");
        }
    }

    public static void IF_ICMPLEtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a > 999) {
            if (b == 2000) {
                System.out.println("branch 1");
            }
            System.out.println("branch 2");
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
        }
    }


    public static void IF_ICMPLTtest(int a, int b) {
        if (isZero(a, b)) {
            return;
        }

        if (a >= 999) {
            System.out.println("branch 1");
            if (b == 2000) {
                System.out.println("branch 2");
            }
        } else {
            if (b == 2001) {
                System.out.println("branch 3");
            }
        }
    }

    public static void LCMPtest(long a) {
        if (a == 123123) {
            System.out.println("branch 1");
        }
        if (a > 123123) {
            System.out.println("branch 2");
        }
        if (a < -123123) {
            System.out.println("branch 3");
        }
    }

    public static void LCMPtest2(long a) {
        if (a != 123123) {
            System.out.println("branch 1");
        }
        if (a >= 123123) {
            System.out.println("branch 2");
        }
        if (a <= -123123) {
            System.out.println("branch 3");
        }
    }

    public static boolean isZero(int a, int b) {
        return a == 0 || b == 0;
    }

    public static void LDCtest(int a) {
        if (a == 9999999) {
            System.out.println("branch 1");
        } else if (a == 6666666) {
            {
                System.out.println("branch 2");
            }
        }
    }
}
