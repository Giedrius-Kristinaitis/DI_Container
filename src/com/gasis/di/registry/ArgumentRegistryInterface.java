package com.di.registry;

public interface ArgumentRegistryInterface {

    <T> void registerArgument(Class clazz, String name, Class<T> type, T value);

    void unregisterArgument(Class clazz, String name);

    <T> T getArgument(Class clazz, String name);

    boolean hasArgumentRegistered(Class clazz, String name);

    Class<?> getArgumentType(Class clazz, String name);
}
