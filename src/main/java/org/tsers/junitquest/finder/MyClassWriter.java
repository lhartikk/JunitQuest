package org.tsers.junitquest.finder;


import org.objectweb.asm.ClassWriter;

public class MyClassWriter extends ClassWriter {
    private ClassLoader classLoader;

    public MyClassWriter(int i, ClassLoader loader) {
        super(i);
        this.classLoader = loader;
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        Class c, d;
        try {
            c = classLoader.loadClass(type1.replace('/', '.'));
            d = classLoader.loadClass(type2.replace('/', '.'));
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }
}
