package com.di.resolver;

public interface ResolverInterface<V, T> {

    T resolve(V v);
}
