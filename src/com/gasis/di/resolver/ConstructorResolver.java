package com.gasis.di.resolver;

import com.gasis.di.registry.ArgumentRegistryInterface;
import com.gasis.di.registry.PreferenceRegistryInterface;
import com.gasis.di.validator.ValidatorInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ConstructorResolver implements ResolverInterface<Class, Constructor<?>> {

    private final ArgumentRegistryInterface argumentRegistry;
    private final PreferenceRegistryInterface preferenceRegistry;
    private final ValidatorInterface<Constructor<?>> dependencyValidator;

    public ConstructorResolver(ArgumentRegistryInterface argumentRegistry, PreferenceRegistryInterface preferenceRegistry, ValidatorInterface<Constructor<?>> dependencyValidator) {
        this.argumentRegistry = argumentRegistry;
        this.preferenceRegistry = preferenceRegistry;
        this.dependencyValidator = dependencyValidator;
    }

    @Override
    public Constructor<?> resolve(Class clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        return getValidConstructor(clazz, constructors);
    }

    private Constructor<?> getValidConstructor(Class clazz, Constructor<?>[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                continue;
            }

            if (!dependencyValidator.validate(clazz, constructor)) {
                throw new RuntimeException("Circular dependency detected: type '" + clazz.getName() + "' parameters depend on the type itself, or type parameter objects themselves have a circular dependency");
            }

            if (parametersForConstructorRegistered(clazz, constructor)) {
                return constructor;
            }
        }

        return getNoParameterConstructor(constructors);
    }

    private boolean parametersForConstructorRegistered(Class clazz, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();

        for (Parameter parameter : parameters) {
            if (preferenceRegistry != null && preferenceRegistry.hasPreference(parameter.getType())) {
                continue;
            }

            if (argumentRegistry != null
                    && argumentRegistry.hasArgumentRegistered(parameter.getType(), parameter.getName())
                    && argumentRegistry.getArgumentType(clazz, parameter.getName()).getName().equals(parameter.getType().getName())) {
                continue;
            }

            Constructor<?>[] parameterConstructors = parameter.getType().getConstructors();

            if (parameterConstructors.length == 0 || getNoParameterConstructor(parameterConstructors) != null) {
                continue;
            }

            return false;
        }

        return true;
    }

    private Constructor<?> getNoParameterConstructor(Constructor<?>[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }

        return null;
    }
}
