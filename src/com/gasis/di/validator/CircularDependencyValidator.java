package com.gasis.di.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class CircularDependencyValidator implements ValidatorInterface<Constructor<?>> {

    @Override
    public boolean validate(Class clazz, Constructor<?> validatable) {
        Parameter[] parameters = validatable.getParameters();

        for (Parameter parameter : parameters) {
            if (parameter.getType().getName().equals(clazz.getName())) {
                return false;
            }

            Constructor<?>[] parameterConstructors = parameter.getType().getConstructors();

            for (Constructor<?> constructor : parameterConstructors) {
                if (!validate(clazz, constructor)) {
                    return false;
                }
            }
        }

        return true;
    }
}
