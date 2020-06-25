package com.di;

import com.di.provider.NotInstantiableTypeProvider;
import com.di.registry.ArgumentRegistryInterface;
import com.di.registry.PreferenceRegistryInterface;
import com.di.resolver.ArgumentResolver;
import com.di.resolver.ConstructorResolver;
import com.di.resolver.ParameterNameResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ObjectManager implements ObjectManagerInterface {

    private final PreferenceRegistryInterface preferenceRegistry;
    private final ArgumentRegistryInterface argumentRegistry;
    private final ConstructorResolver constructorResolver;
    private final ArgumentResolver argumentResolver;
    private final ParameterNameResolver parameterNameResolver;
    private final Map<Class, Object> instantiatedObjects;

    public ObjectManager(PreferenceRegistryInterface preferenceRegistry) {
        this(preferenceRegistry, null);
    }

    public ObjectManager(PreferenceRegistryInterface preferenceRegistry, ArgumentRegistryInterface argumentRegistry) {
        this.preferenceRegistry = preferenceRegistry;
        this.argumentRegistry = argumentRegistry;

        this.parameterNameResolver = new ParameterNameResolver();
        this.constructorResolver = new ConstructorResolver(argumentRegistry, preferenceRegistry, parameterNameResolver, new NotInstantiableTypeProvider());
        this.argumentResolver = new ArgumentResolver(this, argumentRegistry, parameterNameResolver);
        this.instantiatedObjects = new HashMap<Class, Object>();
    }

    @Override
    public Object instantiate(Class clazz) {
        if (clazz == ObjectManagerInterface.class || clazz == ObjectManager.class) {
            return this;
        }

        if (instantiatedObjects.containsKey(clazz)) {
            return instantiatedObjects.get(clazz);
        }

        Class<?> type = getTypePreference(clazz);

        if ((type.isInterface() || Modifier.isAbstract(type.getModifiers())) && !preferenceRegistry.hasPreference(type)) {
            throw new RuntimeException("Cannot instantiate type '" + type.getName() + "': abstract type has no preference");
        }

        int constructorCount = type.getConstructors().length;

        Constructor<?> constructor = constructorResolver.resolve(type);

        if (constructor == null && constructorCount > 0) {
            throw new RuntimeException("Cannot instantiate type '" + type.getName() + "': some class constructor parameters do not have registered arguments");
        }

        return getInstance(type, constructorCount > 0, constructor);
    }

    private Object getInstance(Class type, boolean hasConstructors, Constructor constructor) {
        try {
            Object instance;

            if (!hasConstructors) {
                instance = type.newInstance();
            } else {
                instance = constructor.newInstance(argumentResolver.resolve(constructor));
            }

            instantiatedObjects.put(type, instance);

            return instance;
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Cannot instantiate type '" + type.getName() + "': access modifier prevents creation");
        } catch (Exception exception) {
            throw new RuntimeException("Cannot instantiate type '" + type.getName() + "': " + exception.getMessage());
        }
    }

    private Class<?> getTypePreference(Class clazz) {
        if (!preferenceRegistry.hasPreference(clazz)) {
            return clazz;
        }

        return preferenceRegistry.getPreference(clazz);
    }
}
