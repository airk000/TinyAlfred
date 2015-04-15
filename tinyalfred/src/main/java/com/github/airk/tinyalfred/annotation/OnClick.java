package com.github.airk.tinyalfred.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kevin on 15/3/19.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerDeclare(setterName = "setOnClickListener",
        setterParam = "View.OnClickListener()",
        listenerName = "onClick")
public @interface OnClick {
    int[] value() default {View.NO_ID};
}
