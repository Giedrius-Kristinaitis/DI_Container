package com.di.resolver;

import com.di.provider.NotInstantiableTypeProvider;
import com.di.registry.ArgumentRegistryInterface;
import com.di.registry.PreferenceRegistryInterface;

import java.lang.reflect.Constructor;

public class ConstructorResolver {

    private final ArgumentRegistryInterface argumentRegistry;
    private final PreferenceRegistryInterface preferenceRegistry;
    private final ParameterNameResolver parameterNameResolver;
    private final NotInstantiableTypeProvider notInstantiableTypeProvider;

    public ConstructorResolver(ArgumentRegistryInterface argumentRegistry, PreferenceRegistryInterface preferenceRegistry, ParameterNameResolver parameterNameResolver, NotInstantiableTypeProvider notInstantiableTypeProvider) {
        this.argumentRegistry = argumentRegistry;
        this.preferenceRegistry = preferenceRegistry;
        this.parameterNameResolver = parameterNameResolver;
        this.notInstantiableTypeProvider = notInstantiableTypeProvider;
    }

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
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        String[] parameterNames = parameterNameResolver.resolve(constructor);

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String parameterName = parameterNames != null ? parameterNames[i] : null;

            if (preferenceRegistered(parameterType)
                    || argumentRegistered(clazz, parameterType, parameterName)
                    || !isTypeInstantiable(parameterType)) {
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

    private boolean preferenceRegistered(Class<?> type) {
        return preferenceRegistry != null && preferenceRegistry.hasPreference(type);
    }

    private boolean argumentRegistered(Class clazz, Class<?> type, String parameterName) {
        return parameterName != null
                && argumentRegistry != null
                && argumentRegistry.hasArgumentRegistered(clazz, parameterName)
                && argumentRegistry.getArgumentType(clazz, parameterName).getName().equals(type.getName());
    }

    private Constructor<?> getNoParameterConstructor(Constructor<?>[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }

        return null;
    }
}
