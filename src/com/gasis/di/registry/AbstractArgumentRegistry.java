package com.gasis.di.registry;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractArgumentRegistry implements ArgumentRegistryInterface {

    private final Map<Class, Map<String, ValueWrapper>> registry;

    public AbstractArgumentRegistry() {
        registry = new HashMap<Class, Map<String, ValueWrapper>>();

        initialize();
    }

    @Override
    public <T> void registerArgument(Class clazz, String name, Class<T> type, T value) {
        Map<String, ValueWrapper> argumentMap = registry.get(clazz);

        if (argumentMap == null) {
            argumentMap = new HashMap<String, ValueWrapper>();
            registry.put(clazz, argumentMap);
        }

        if (argumentMap.containsKey(name)) {
            throw new IllegalArgumentException("Cannot register argument: type '" + clazz.getName() + "' already has argument '" + name + "' registered");
        }

        argumentMap.put(name, new ValueWrapper<T>(type, value));
    }

    @Override
    public void unregisterArgument(Class clazz, String name) {
        Map<String, ValueWrapper> argumentMap = registry.get(clazz);

        if (argumentMap == null) {
            throw new IllegalArgumentException("Cannot unregister argument: no arguments registered for type '" + clazz.getName() + "'");
        }

        if (argumentMap.containsKey(name)) {
            throw new IllegalArgumentException("Cannot unregister argument: type '" + clazz.getName() + "' does not have argument '" + name + "' registered");
        }

        argumentMap.remove(name);

        if (!argumentMap.isEmpty()) {
            return;
        }

        registry.remove(clazz);
    }

    @Override
    public <T> T getArgument(Class clazz, String name) {
        Map<String, ValueWrapper> argumentMap = registry.get(clazz);

        if (argumentMap == null) {
            throw new IllegalArgumentException("Cannot get argument: no arguments found for type '" + clazz.getName() + "'");
        }

        ValueWrapper value = argumentMap.get(name);

        if (value == null) {
            throw new IllegalArgumentException("Cannot get argument: type '" + clazz.getName() + "' does not have argument '" + name + "' registered");
        }

        Class<T> type = value.type;

        return type.cast(value.value);
    }

    @Override
    public boolean hasArgumentRegistered(Class clazz, String name) {
        Map<String, ValueWrapper> argumentMap = registry.get(clazz);

        if (argumentMap == null) {
            return false;
        }

        return argumentMap.containsKey(name);
    }

    @Override
    public Class<?> getArgumentType(Class clazz, String name) {
        Map<String, ValueWrapper> argumentMap = registry.get(clazz);

        if (argumentMap == null) {
            throw new IllegalArgumentException("Cannot get argument type: no arguments found for type '" + clazz.getName() + "'");
        }

        ValueWrapper value = argumentMap.get(name);

        if (value == null) {
            throw new IllegalArgumentException("Cannot get argument type: type '" + clazz.getName() + "' does not have argument '" + name + "' registered");
        }

        return value.type;
    }

    protected abstract void initialize();

    protected class ValueWrapper<T> {

        protected Class<T> type;
        protected T value;

        protected ValueWrapper(Class<T> type, T value) {
            this.type = type;
            this.value = value;
        }
    }
}
