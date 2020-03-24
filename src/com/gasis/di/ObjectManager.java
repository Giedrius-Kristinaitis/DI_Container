package com.gasis.di;

import com.gasis.di.registry.ArgumentRegistryInterface;
import com.gasis.di.registry.PreferenceRegistryInterface;
import com.gasis.di.resolver.ArgumentResolver;
import com.gasis.di.resolver.ConstructorResolver;
import com.gasis.di.resolver.ResolverInterface;
import com.gasis.di.validator.CircularDependencyValidator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ObjectManager implements ObjectManagerInterface {

    private final PreferenceRegistryInterface preferenceRegistry;
    private final ArgumentRegistryInterface argumentRegistry;
    private final ResolverInterface<Class, Constructor<?>> constructorResolver;
    private final ResolverInterface<Constructor<?>, Object[]> argumentResolver;
    private final Map<Class, Object> instantiatedObjects;

    public ObjectManager(PreferenceRegistryInterface preferenceRegistry) {
        this(preferenceRegistry, null);
    }

    public ObjectManager(PreferenceRegistryInterface preferenceRegistry, ArgumentRegistryInterface argumentRegistry) {
        this.preferenceRegistry = preferenceRegistry;
        this.argumentRegistry = argumentRegistry;

        this.constructorResolver = new ConstructorResolver(argumentRegistry, preferenceRegistry, new CircularDependencyValidator());
        this.argumentResolver = new ArgumentResolver(this, argumentRegistry);
        this.instantiatedObjects = new HashMap<Class, Object>();
    }

    @Override
    public Object instantiate(Class clazz) {
        if (instantiatedObjects.containsKey(clazz)) {
            return instantiatedObjects.get(clazz);
        }

        Class<?> type = getTypePreference(clazz);

        int constructorCount = type.getConstructors().length;

        Constructor<?> constructor = constructorResolver.resolve(type);

        if (constructor == null && constructorCount > 0) {
            throw new RuntimeException("Type error occurred when instantiating object '" + type.getName() + "'");
        }

        try {
            Object instance;

            if (constructorCount == 0) {
                instance = type.newInstance();
            } else {
                instance = constructor.newInstance(argumentResolver.resolve(constructor));
            }

            instantiatedObjects.put(clazz, instance);

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
