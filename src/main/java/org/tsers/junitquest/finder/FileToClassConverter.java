package org.tsers.junitquest.finder;

import java.io.File;

public class FileToClassConverter {

    private final String classPathRoot;

    private final ClassLoader classLoader;

    public FileToClassConverter(String classPathRoot, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classPathRoot = classPathRoot;
    }


    public Class convertToClass(File classFile) {
        Class classInstance = null;
        if (classFile.getAbsolutePath().startsWith(classPathRoot) && classFile.getAbsolutePath().endsWith(".class")) {
            classInstance = getClassFromName(classFile.getAbsolutePath());
        }
        return classInstance;
    }

    private Class getClassFromName(String fileName) {
        try {
            String className = removeClassPathBase(fileName);
            className = removeExtension(className);
            return classLoader.loadClass(className);
        } catch (Exception e) {
            return null;
        }
    }

    private String removeClassPathBase(String fileName) {
        int prefix = 0;
        if (!classPathRoot.endsWith("/")) {
            prefix++;
        }
        String classPart = fileName.substring(classPathRoot.length() + prefix);
        String className = classPart.replace(File.separatorChar, '.');
        return className;
    }


    public static String removeExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String modifiedName = fileName;
        int index = fileName.lastIndexOf(".");
        if (index > -1) {
            modifiedName = fileName.substring(0, index);
        }
        return modifiedName;
    }
}
