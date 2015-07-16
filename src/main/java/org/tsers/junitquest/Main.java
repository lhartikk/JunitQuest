package org.tsers.junitquest;

import java.io.*;

public class Main {

    public static void main(String args[]) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: junitquest.jar CLASSNAME ABSOLUTE_BYTECODEROOTPATH [ OUTPUTFILENAME ]");
            return;
        }
        String className = args[0];
        String bytecodeRootDirectory = args[1];
        String outputFile = getOutputFilename(args);

        TestGenerator tg = new TestGenerator();
        String testCase = tg.generateTests(className, bytecodeRootDirectory);

        writeToFile(testCase, outputFile);
        System.out.println("Created file: " + outputFile);
    }

    private static String getOutputFilename(String args[]) {
        if (args.length > 2) {
            return args[2];
        } else {
            String className = args[0];
            return getDefaultOutputFilename(className);
        }
    }

    private static String getDefaultOutputFilename(String className) {
        return Jutil.getClassNameFromPackage(className) + "Test.java";
    }

    private static void writeToFile(String text, String filename) {
        BufferedWriter writer = null;
        try {
            File file = new File(filename);

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

}
