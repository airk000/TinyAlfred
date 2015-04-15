package com.github.airk.tinyalfred.internal;

import com.github.airk.tinyalfred.annotation.OnClick;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Created by kevin on 15/3/19.
 */
class ClickHandler extends ListenerBaseHandler {

    @Override
    protected String cleanHook(String var) {
        return null;
    }

    @Override
    protected int[] getIds(Element e) {
        return e.getAnnotation(OnClick.class).value();
    }

    @Override
    protected ListenerModel.Type getType() {
        return ListenerModel.Type.CLICK;
    }

    @Override
    protected String getInterfaceName() {
        return "handleClick";
    }

    @Override
    public Class<? extends Annotation> canHandle() {
        return OnClick.class;
    }
}
