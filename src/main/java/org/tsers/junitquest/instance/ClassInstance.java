package org.tsers.junitquest.instance;


public class ClassInstance implements Instance {

    final Class clazz;

    public ClassInstance(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object build() {
        return clazz;
    }

    @Override
    public Object getInstance() {
        return Class.class;
    }

    @Override
    public String asString() {
        if (clazz.getName().equals("int")) {
            return "Integer.TYPE";
        } else if (clazz.getName().equals("boolean")) {
            return "Boolean.TYPE";
        } else if (clazz.getName().equals("double")) {
            return "Double.TYPE";
        } else if (clazz.getName().equals("float")) {
            return "Float.TYPE";
        } else if (clazz.getName().equals("long")) {
            return "Long.TYPE";
        } else if (clazz.getName().equals("short")) {
            return "Short.TYPE";
        } else if (clazz.getName().equals("char")) {
            return "Character.TYPE";
        } else if (clazz.getName().equals("byte")) {
            return "Byte.TYPE";
        }
        return clazz.getName() + ".class";
    }
}
