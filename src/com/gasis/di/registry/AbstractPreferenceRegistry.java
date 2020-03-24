package com.gasis.di.registry;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPreferenceRegistry implements PreferenceRegistryInterface {

    private final Map<Class, Class> registry;

    public AbstractPreferenceRegistry() {
        registry = new HashMap<Class, Class>();

        initialize();
    }

    @Override
    public void registerPreference(Class abstraction, Class implementation) {
        if (registry.containsKey(abstraction)) {
            throw new IllegalArgumentException("Preference for type '" + abstraction.getName() + "' already exists");
        }

        registry.put(abstraction, implementation);
    }

    @Override
    public void unregisterPreference(Class abstraction) {
        if (!registry.containsKey(abstraction)) {
            throw new IllegalArgumentException("Trying to unregister preference for type '" + abstraction.getName() + "' that does not exist");
        }

        registry.remove(abstraction);
    }

    @Override
    public Class getPreference(Class abstraction) {
        Class preference = registry.get(abstraction);

        if (preference == null) {
            throw new IllegalArgumentException("Preference for type '" + abstraction.getName() + "' does not exist");
        }

        return preference;
    }

    @Override
    public boolean hasPreference(Class abstraction) {
        return registry.containsKey(abstraction);
    }

    protected abstract void initialize();
}
