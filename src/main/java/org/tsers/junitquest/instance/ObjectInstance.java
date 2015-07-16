package org.tsers.junitquest.instance;

import org.tsers.junitquest.Jutil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectInstance implements Instance {

    final AccessibleObject method;
    final Object classInstance;
    final List<Instance> parameters;

    public ObjectInstance(Method method, Object classInstance, List<Instance> parameters) {
        this.method = method;
        this.classInstance = classInstance;
        this.parameters = parameters;
    }

    public ObjectInstance(Constructor constructor, List<Instance> parameters) {
        this.method = constructor;
        this.classInstance = null;
        this.parameters = parameters;
    }


    public AccessibleObject getMethod() {
        return this.method;
    }

    public List<Instance> getParameters() {
        return this.parameters;
    }

    @Override
    public Object build() {
        try {
            List<Object> builtParameters = parameters.stream().map(p -> p.build()).collect(Collectors.toList());
            Object oParameters[] = Jutil.listToArray(builtParameters);
            if (method instanceof Method) {
                return ((Method) method).invoke(classInstance, oParameters);
            } else if (method instanceof Constructor) {
                return ((Constructor) method).newInstance(oParameters);
            } else {
                throw new RuntimeException("Cannot build instance");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getInstance() {
        return classInstance;
    }


    @Override
    public String asString() {

        if (method instanceof Constructor) {
            return this.toString();
        } else {
            return "asdf";
        }
    }


}
