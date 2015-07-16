package org.tsers.junitquest;

import org.tsers.junitquest.finder.JavaClassFinder;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstanceHelper {


    static ClassLoader classLoader;
    final JavaClassFinder classFinder;

    private static InstanceHelper instanceHelper;


    public static void init(ClassLoader classLoader, String bytecodeDir) {
        instanceHelper = new InstanceHelper(classLoader, bytecodeDir);
    }

    public static InstanceHelper get() {
        if (instanceHelper == null) {
            throw new RuntimeException("Init me first");
        }
        return instanceHelper;
    }

    private InstanceHelper(ClassLoader classLoader, String bytecodeDir) {
        classFinder = new JavaClassFinder(classLoader, bytecodeDir);
    }

    public static void setClassLoader(ClassLoader loader) {
        classLoader = loader;
    }

    public List<Class> getAllImplementingClasses(String rawClassName) {

        final int numberOfArrays = getNumberOfArrays(rawClassName);
        String className = parseClassName(rawClassName);

        try {
            List<Class> clazzes = getImplementingClasses(className);
            return clazzes.stream()
                    .map(c -> wrapToArrayIfNeeded(c, numberOfArrays))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("class not found!");
        }

    }

    private String parseClassName(String className) {
        while (className.startsWith("[")) {
            className = className.substring(1);
        }
        if (className.startsWith("L")) {
            className = className.substring(1);
        }
        if (className.endsWith(";")) {
            className = className.substring(0, className.length() - 1);
        }
        className = className.replace('/', '.');
        return className;
    }

    private List<Class> getImplementingClasses(String className) throws ClassNotFoundException {
        if (className.length() == 1) {
            return Arrays.asList(Jutil.primitiveDescToClass(className));
        }

        Class clazz = classLoader.loadClass(className);
        List<Class> clazzes = classFinder.findAllMatchingTypes(clazz).stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> getLoadedClass(c))
                .collect(Collectors.toList());

        clazzes.add(clazz);
        return clazzes;
    }

    private static int getNumberOfArrays(String className) {
        int numberOfArrays = 0;
        while (className.startsWith("[")) {
            numberOfArrays++;
            className = className.substring(1);
        }
        return numberOfArrays;
    }

    private static Class wrapToArrayIfNeeded(Class clazz, int numberOfArrays) {
        while (numberOfArrays > 0) {
            numberOfArrays--;
            clazz = Array.newInstance(clazz, 1).getClass();

        }
        return clazz;
    }

    private Class<?> getLoadedClass(Class<?> c) {
        try {
            return classLoader.loadClass(c.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found!");
        }

    }

}
