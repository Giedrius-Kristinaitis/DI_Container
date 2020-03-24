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

    public ArgumentResolver(ObjectManagerInterface objectManager, ArgumentRegistryInterface argumentRegistry) {
        this.objectManager = objectManager;
        this.argumentRegistry = argumentRegistry;
    }

    @Override
    public Object[] resolve(Constructor<?> constructor) {
        List<Object> arguments = new ArrayList<Object>();
        Parameter[] parameters = constructor.getParameters();

        for (Parameter parameter : parameters) {
            if (argumentRegistry.hasArgumentRegistered(constructor.getDeclaringClass(), parameter.getName())) {
                arguments.add(argumentRegistry.getArgument(constructor.getDeclaringClass(), parameter.getName()));
                continue;
            }

            arguments.add(objectManager.instantiate(parameter.getType()));
        }

        return arguments.toArray();
    }
}
