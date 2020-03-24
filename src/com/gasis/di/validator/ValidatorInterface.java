package com.gasis.di.validator;

public interface ValidatorInterface<T> {

    boolean validate(Class clazz, T validatable);
}
