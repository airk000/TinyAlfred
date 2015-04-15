package com.github.airk.tinyalfred.internal;

import com.github.airk.tinyalfred.annotation.OnLongClick;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Created by kevin on 15/4/10.
 */
class LongClickHandler extends ListenerBaseHandler {
    @Override
    public Class<? extends Annotation> canHandle() {
        return OnLongClick.class;
    }

    @Override
    protected String cleanHook(String var) {
        return null;
    }

    @Override
    protected int[] getIds(Element e) {
        return e.getAnnotation(OnLongClick.class).value();
    }

    @Override
    protected ListenerModel.Type getType() {
        return ListenerModel.Type.LONG_CLICK;
    }

    @Override
    protected String getInterfaceName() {
        return "handleLongClick";
    }
}
