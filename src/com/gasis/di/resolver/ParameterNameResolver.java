package com.di.resolver;

import com.di.annotation.Parameters;

import java.lang.reflect.Constructor;
import java.util.*;

public class ParameterNameResolver implements ResolverInterface<Constructor<?>, String[]> {

    private final Map<Constructor<?>, String[]> parameterNameCache;

    public ParameterNameResolver() {
        this.parameterNameCache = new HashMap<Constructor<?>, String[]>();
    }

    @Override
    public String[] resolve(Constructor<?> constructor) {
        if (parameterNameCache.containsKey(constructor)) {
            return parameterNameCache.get(constructor);
        }

        String[] names = listToArray(getParameterNames(constructor));

        parameterNameCache.put(constructor, names);

        return names;
    }

    private List<String> getParameterNames(Constructor<?> constructor) {
        Parameters annotationParameters = constructor.getAnnotation(Parameters.class);

        if (annotationParameters == null) {
            return getConstructorParameterNames(constructor);
        }

        String[] annotationParameterNames = annotationParameters.value();

        List<String> names = getParameterNamesForInnerClasses(constructor, constructor.getParameterTypes().length - annotationParameterNames.length);

        Collections.addAll(names, annotationParameterNames);

        return names;
    }

    private List<String> getParameterNamesForInnerClasses(Constructor<?> constructor, int count) {
        List<String> names = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            names.add("arg" + i);
        }

        return names;
    }

    private List<String> getConstructorParameterNames(Constructor<?> constructor) {
        List<String> names = new ArrayList<String>();

        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            names.add("arg" + i);
        }

        return names;
    }

    private String[] listToArray(List<String> list) {
        String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }
}
