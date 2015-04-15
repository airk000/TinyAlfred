package com.github.airk.tinyalfred.internal;

import javax.lang.model.element.Element;

import java.lang.annotation.Annotation;

/**
 * Created by kevin on 15/3/19.
 */
interface Handler {
    Class<? extends Annotation> canHandle();

    void handle(Processor processor, Element e);

    String generateCode(Processor processor, String fullName);
}
