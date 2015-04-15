package com.github.airk.tinyalfred.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kevin on 15/3/23.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD})
/**
 * Note with other annotation, means this view may be nullable.
 */
public @interface NullableView {
}
