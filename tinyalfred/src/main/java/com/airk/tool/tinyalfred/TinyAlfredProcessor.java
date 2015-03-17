package com.airk.tool.tinyalfred;

import com.airk.tool.tinyalfred.annotation.FindView;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kevin on 15/3/17.
 */
public class TinyAlfredProcessor extends AbstractProcessor {

    Elements elementUtil;
    Filer filer;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtil = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<String>();
        set.add(FindView.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element e : env.getElementsAnnotatedWith(FindView.class)) {
            debugLog(e.asType());
            debugLog(e.getSimpleName());
            debugLog(e.getModifiers());
        }
        return true;
    }

    void debugLog(Object o) {
        if (o instanceof String || o instanceof CharSequence) {
            messager.printMessage(Diagnostic.Kind.NOTE, (CharSequence) o);
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, o.toString());
        }
    }
}
