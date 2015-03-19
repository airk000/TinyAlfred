package com.airk.tool.tinyalfred;

/**
 * Created by kevin on 15/3/17.
 */
public interface Alfred<T> {
    public void handleViews(T belong, Object root);
}
