package com.github.airk.tinyalfred.internal;

import com.github.airk.tinyalfred.annotation.OnPreDraw;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Created by kevin on 15/4/10.
 */
class PreDrawHandler extends ListenerBaseHandler {

    @Override
    protected String cleanHook(String var) {
        StringBuilder sb = new StringBuilder();
        sb.append("                ").append(var).append(".getViewTreeObserver().removeOnPreDrawListener(this);\n");
        sb.append("                return true;\n");
        return sb.toString();
    }

    @Override
    protected int[] getIds(Element e) {
        int[] ids = new int[1];
        ids[0] = e.getAnnotation(OnPreDraw.class).value();
        return ids;
    }

    @Override
    protected ListenerModel.Type getType() {
        return ListenerModel.Type.PRE_DRAW;
    }

    @Override
    protected String getInterfaceName() {
        return "handlePreDraw";
    }

    @Override
    public Class<? extends Annotation> canHandle() {
        return OnPreDraw.class;
    }
}
