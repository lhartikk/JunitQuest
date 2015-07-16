package org.tsers.junitquest.instance;

public class PrimitiveInstance implements Instance {

    private final Object instance;

    @Override
    public Object build() {
        return instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public String asString() {
        if (instance instanceof Character) {
            return "'" + instance + "'";
        }
        return instance.toString();
    }

    public PrimitiveInstance(int i) {
        this.instance = new Integer(i);
    }

    public PrimitiveInstance(boolean b) {
        this.instance = new Boolean(b);
    }

    public PrimitiveInstance(char c) {
        this.instance = new Character(c);
    }

    public PrimitiveInstance(short s) {
        this.instance = new Short(s);
    }

    public PrimitiveInstance(byte b) {
        this.instance = new Byte(b);
    }

    public PrimitiveInstance(long l) {
        this.instance = new Long(l);
    }

    public PrimitiveInstance(float f) {
        this.instance = new Float(f);
    }

    public PrimitiveInstance(double d) {
        this.instance = new Double(d);
    }

}
