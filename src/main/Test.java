package main;

import com.gasis.di.registry.AbstractArgumentRegistry;

import java.lang.reflect.Constructor;

public class Test {

    public static void main(String[] args) throws Exception {
        A a = new A();

        int arg = a.getArgument(Object.class, "ass");

        System.out.println(arg);

        Constructor<?>[] c = A.class.getConstructors();

        System.out.println(c.length);
    }

    static class A extends AbstractArgumentRegistry {

        @Override
        protected void initialize() {
            registerArgument(Object.class, "ass", Integer.class, 5);
        }
    }
}
