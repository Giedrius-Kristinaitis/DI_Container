package com.gasis.di.resolver;

import com.gasis.di.ObjectManagerInterface;
import com.gasis.di.registry.ArgumentRegistryInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ArgumentResolver implements ResolverInterface<Constructor<?>, Object[]> {

    private final ObjectManagerInterface objectManager;
    private final ArgumentRegistryInterface argumentRegistry;
    private final ResolverInterface<Constructor<?>, String[]> parameterNameResolver;

    public ArgumentResolver(ObjectManagerInterface objectManager, ArgumentRegistryInterface argumentRegistry, ResolverInterface<Constructor<?>, String[]> parameterNameResolver) {
        this.objectManager = objectManager;
        this.argumentRegistry = argumentRegistry;
        this.parameterNameResolver = parameterNameResolver;
    }

    @Override
    public Object[] resolve(Constructor<?> constructor) {
        List<Object> arguments = new ArrayList<Object>();
        Parameter[] parameters = constructor.getParameters();
        String[] parameterNames = parameterNameResolver.resolve(constructor);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames != null ? parameterNames[i] : null;

            if (parameterName != null && argumentRegistry.hasArgumentRegistered(constructor.getDeclaringClass(), parameterName)) {
                addArgumentToList(arguments, constructor, parameterName);
                continue;
            }

            arguments.add(objectManager.instantiate(parameter.getType()));
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
