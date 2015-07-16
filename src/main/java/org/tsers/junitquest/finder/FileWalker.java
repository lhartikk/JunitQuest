package org.tsers.junitquest.finder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileWalker {

    private final String baseDir;

    private final FileToClassConverter converter;

    private final Class toFind;

    public FileWalker(String baseDir, ClassLoader cl, Class toFind) {
        this.baseDir = baseDir;
        this.toFind = toFind;
        converter = new FileToClassConverter(baseDir, cl);
    }

    public List<Class> walk() {
        File rootDir = new File(baseDir);
        return walk(rootDir);
    }

    protected List<Class> walk(File currentDir) {
        File[] files = currentDir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        List<Class> found = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                found.addAll(walk(file));
            } else {
                found.addAll(handleFile(file));
            }
        }
        return found;
    }

    public List<Class<?>> handleFile(File file) {
        try {
            Class<?> clazz = converter.convertToClass(file);
            if (clazz == null) {
                Arrays.asList();
            }
            Object ifaces[] = clazz.getInterfaces();
            for (Object iface : ifaces) {
                String siface = iface.toString();
                String sfinding = toFind.toString();

                if (siface.equals(sfinding)) {
                    return Arrays.asList(clazz);
                }
            }

            if (toFind.isAssignableFrom(clazz)) {
                return Arrays.asList(clazz);
            }
        } catch (Exception e) {

        }
        return Arrays.asList();
    }


}
