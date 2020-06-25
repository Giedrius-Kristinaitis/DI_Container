package com.di.resolver;

import com.di.ObjectManagerInterface;
import com.di.registry.ArgumentRegistryInterface;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArgumentResolver {

    private final ObjectManagerInterface objectManager;
    private final ArgumentRegistryInterface argumentRegistry;
    private final ParameterNameResolver parameterNameResolver;

    public ArgumentResolver(ObjectManagerInterface objectManager, ArgumentRegistryInterface argumentRegistry, ParameterNameResolver parameterNameResolver) {
        this.objectManager = objectManager;
        this.argumentRegistry = argumentRegistry;
        this.parameterNameResolver = parameterNameResolver;
    }
    
    public Object[] resolve(Constructor<?> constructor) {
        List<Object> arguments = new ArrayList<Object>();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        String[] parameterNames = parameterNameResolver.resolve(constructor);

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String parameterName = parameterNames != null ? parameterNames[i] : null;

            if (parameterName != null && argumentRegistry.hasArgumentRegistered(constructor.getDeclaringClass(), parameterName)) {
                addArgumentToList(arguments, constructor, parameterName);
                continue;
            }

            arguments.add(objectManager.instantiate(parameterType));
        }

        return arguments.toArray();
    }

    private void addArgumentToList(List<Object> arguments, Constructor<?> constructor, String parameterName) {
        Object argument = argumentRegistry.getArgument(constructor.getDeclaringClass(), parameterName);

        if (argument instanceof Class<?>) {
            argument = objectManager.instantiate((Class<?>) argument);
        }

        arguments.add(argument);
    }
}
