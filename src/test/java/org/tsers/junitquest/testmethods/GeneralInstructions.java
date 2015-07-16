package org.tsers.junitquest.testmethods;


import org.tsers.junitquest.testgenerator.MyInteger;

public class GeneralInstructions {


    public static void IFNULLtest(MyInteger myInteger, int b) {
        if (myInteger != null) {
            System.out.println("branch 1");
            if (b == 333) {
                System.out.println("branch 2");
            } else {
                System.out.println("branch 3");
            }
        } else {
            System.out.println("branch 4");
            if (b == 666) {
                System.out.println("branch 5");
            } else {
                System.out.println("branch 6");
            }
        }
    }


    public static void IFNONNULLtest(MyInteger myInteger, int b) {
        if (myInteger == null) {
            System.out.println("branch 1");
            if (b == 333) {
                System.out.println("branch 2");
            } else {
                System.out.println("branch 3");
            }
        } else {
            System.out.println("branch 4");
            if (b == 333) {
                System.out.println("branch 5");
            } else {
                System.out.println("branch 6");
            }
        }
    }

    public static void ACONST_NULLtest(MyInteger myInteger, int b) {
        Object o = null;
        if (myInteger == o) {
            System.out.println("branch 1");
            if (b == 333) {
                System.out.println("branch 2");
            } else {
                System.out.println("branch 3");
            }
        } else {
            System.out.println("branch 4");
            if (b == 333) {
                System.out.println("branch 5");
            } else {
                System.out.println("branch 6");
            }
        }
    }

    public static int TABLESWITCHtest(int a) {
        switch (a) {
            case -1:
                return 0;
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 0;
            default:
                return 0;

        }
    }

    public static void INSTANCEOFtest(Object a) {
        if (a instanceof Integer) {
            System.out.println("branch 1");
        } else if (a instanceof String) {
            System.out.println("branch 2");
        }
    }

    public static void CHECKCASTtest(Object a, int b) {
        Integer asdf = (Integer) a;
        if (b == 2) {
            System.out.println("branch 1");
        } else {
            System.out.println("branch 2");
        }
    }


}
