package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.annotation.OnPreDraw;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

/**
 * Created by kevin on 15/4/10.
 */
class PreDrawHandler implements Handler {
    @Override
    public Class<? extends Annotation> canHandle() {
        return OnPreDraw.class;
    }

    @Override
    public void handle(Processor processor, Element e) {

    }

    @Override
    public String generateCode(Processor processor, String fullName) {
        return null;
    }
}
