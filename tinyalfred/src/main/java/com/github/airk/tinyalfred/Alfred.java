package com.github.airk.tinyalfred;

/**
 * Created by kevin on 15/3/17.
 */
public interface Alfred<T> {
    void findViews(final T belong, Object root);
    void handleClick(final T belong, Object root);
    void handleLongClick(final T belong, Object root);
    void handlePreDraw(final T belong, Object root);
}
