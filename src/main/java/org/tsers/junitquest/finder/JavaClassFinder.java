package org.tsers.junitquest.finder;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaClassFinder {
    public static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";
    public static final String SUN_BOOT_CLASS_PATH = "sun.boot.class.path";
    private static final String RUNTIME_JAR_TO_SEARCH =  File.separatorChar + "rt.jar";
    public final List<Class> runtimeClasses;

    private static String bytecodeDir;
    private static String runtimeJarLocation;
    private ClassLoader classLoader;

    public JavaClassFinder(ClassLoader loader, String bytecodeDir) {
        this.bytecodeDir = bytecodeDir;
        this.classLoader = loader;

        String runtimeJarLocation = getRuntimeLocation();
        runtimeClasses = solveFromRT(runtimeJarLocation);
    }

    public static void setRuntimeJarLocation(String runtimeJarLocation) {
        JavaClassFinder.runtimeJarLocation = runtimeJarLocation;
    }

    private static String getRuntimeLocation() {

        if (runtimeJarLocation == null) {
            Optional<String> runtimeJarLocation =
                    getClassPathRoots(JAVA_CLASS_PATH_PROPERTY).stream()
                            .filter(c -> c.endsWith(RUNTIME_JAR_TO_SEARCH))
                            .findFirst();
            if (runtimeJarLocation.isPresent()) {
                return runtimeJarLocation.get();
            }
            String runtimeJarLocation2 =
                    getClassPathRoots(SUN_BOOT_CLASS_PATH).stream()
                            .filter(c -> c.endsWith(RUNTIME_JAR_TO_SEARCH))
                            .findFirst().orElse("");


            return runtimeJarLocation2;
        }
        return runtimeJarLocation;
    }

    public List<Class> findAllMatchingTypes(Class toFind) {


        List<Class> foundClasses =
                runtimeClasses.stream()
                        .filter(c -> toFind.isAssignableFrom(c))
                        .collect(Collectors.toList());

        for (String classPathRoot : getClassPathRoots(JAVA_CLASS_PATH_PROPERTY)) {
            if (classPathRoot.endsWith(".jar")) {
                continue;
            }

            FileWalker fileWalker = new FileWalker(classPathRoot, classLoader, toFind);
            List<Class> matchedClasses = fileWalker.walk();
            foundClasses.addAll(matchedClasses);
        }

        return foundClasses;
    }

    private static List<Class> solveFromRT(String classPathRoot) {

        List<Class> classNames = new ArrayList<Class>();
        try {

            ZipInputStream zip = new ZipInputStream(new FileInputStream(classPathRoot));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName().startsWith("java/")
                        && !entry.getName().contains("$")) {
                    String className = entry.getName().replace('/', '.');
                    String n = className.substring(0, className.length() - ".class".length());
                    classNames.add(Class.forName(n));
                }
            }

        } catch (Exception e) {

        }
        return classNames;
    }


    public static List<String> getClassPathRoots(String classPathProperty) {
        String classPath = System.getProperty(classPathProperty);

        classPath = classPath + File.pathSeparator + bytecodeDir;
        String[] pathElements = classPath.split(File.pathSeparator);

        return Arrays.asList(pathElements);
    }

}

