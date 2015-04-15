package com.github.airk.tinyalfred.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kevin on 15/3/23.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ListenerDeclare {
    String setterName();
    String setterParam();
    String listenerName();
    boolean hasViewParam() default true;
    String listenerReturnType() default "void";
    String userReturnType() default "void";
}
