package com.airk.tool.tinyalfred.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kevin on 15/3/23.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerDeclare(setterName = "getViewTreeObserver().addOnPreDrawListener",
        setterParam = "android.view.ViewTreeObserver.OnPreDrawListener()",
        listenerName = "onPreDraw")
public @interface OnPreDraw {
    int value() default View.NO_ID;
}
