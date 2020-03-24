package com.gasis.di.resolver;

import com.gasis.di.provider.TypeProviderInterface;
import com.gasis.di.registry.ArgumentRegistryInterface;
import com.gasis.di.registry.PreferenceRegistryInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ConstructorResolver implements ResolverInterface<Class, Constructor<?>> {

    private final ArgumentRegistryInterface argumentRegistry;
    private final PreferenceRegistryInterface preferenceRegistry;
    private final ResolverInterface<Constructor<?>, String[]> parameterNameResolver;
    private final TypeProviderInterface notInstantiableTypeProvider;

    public ConstructorResolver(ArgumentRegistryInterface argumentRegistry, PreferenceRegistryInterface preferenceRegistry, ResolverInterface<Constructor<?>, String[]> parameterNameResolver, TypeProviderInterface notInstantiableTypeProvider) {
        this.argumentRegistry = argumentRegistry;
        this.preferenceRegistry = preferenceRegistry;
        this.parameterNameResolver = parameterNameResolver;
        this.notInstantiableTypeProvider = notInstantiableTypeProvider;
    }

    @Override
    public Constructor<?> resolve(Class clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        return getValidConstructor(clazz, constructors);
    }

    private Constructor<?> getValidConstructor(Class clazz, Constructor<?>[] constructors) {
        Constructor<?> noParameterConstructor = getNoParameterConstructor(constructors);

        if (noParameterConstructor != null) {
            return noParameterConstructor;
        }

        for (Constructor constructor : constructors) {
            if (parametersForConstructorRegistered(clazz, constructor)) {
                return constructor;
            }
        }

        return null;
    }

    private boolean parametersForConstructorRegistered(Class clazz, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        String[] parameterNames = parameterNameResolver.resolve(constructor);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames != null ? parameterNames[i] : null;

            if (preferenceRegistered(parameter)
                    || argumentRegistered(clazz, parameter, parameterName)
                    || !isTypeInstantiable(parameter.getType())) {
                continue;
            }

            return false;
        }

        return true;
    }

    private boolean isTypeInstantiable(Class type) {
        for (Class<?> notInstantiableType : notInstantiableTypeProvider.getTypes()) {
            if (type == notInstantiableType) {
                return true;
            }
        }

        return false;
    }

    private boolean preferenceRegistered(Parameter parameter) {
        return preferenceRegistry != null && preferenceRegistry.hasPreference(parameter.getType());
    }

    private boolean argumentRegistered(Class clazz, Parameter parameter, String parameterName) {
        return parameterName != null
                && argumentRegistry != null
                && argumentRegistry.hasArgumentRegistered(clazz, parameterName)
                && argumentRegistry.getArgumentType(clazz, parameterName).getName().equals(parameter.getType().getName());
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
