package com.airk.tool.tinyalfred;

/**
 * Created by kevin on 15/3/17.
 */
public interface Alfred<T> {
    public void findViews(final T belong, Object root);
    public void handleListeners(final T belong, Object root);
    public void handlePreDraw(final T belong, Object root);
}
