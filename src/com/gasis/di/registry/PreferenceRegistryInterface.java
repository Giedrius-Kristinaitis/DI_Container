package com.di.registry;

public interface PreferenceRegistryInterface {

    void registerPreference(Class abstraction, Class implementation);

    void unregisterPreference(Class abstraction);

    Class getPreference(Class abstraction);

    boolean hasPreference(Class abstraction);
}
