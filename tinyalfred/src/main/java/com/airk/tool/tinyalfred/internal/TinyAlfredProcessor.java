package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.annotation.FindView;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kevin on 15/3/17.
 */
public class TinyAlfredProcessor extends AbstractProcessor {

    Elements elementUtil;
    Filer filer;
    static Messager messager;

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
        Map<TypeElement, Handler> handlerMap = findTargets(env);

        for (Map.Entry<TypeElement, Handler> entry : handlerMap.entrySet()) {
            TypeElement element = entry.getKey();
            Handler handler = entry.getValue();
            try {
                JavaFileObject fileObject = filer.createSourceFile(handler.getFileName(), element);
                Writer writer = fileObject.openWriter();
                writer.write(handler.getGenerateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                errorLog("Can't write class " + handler.getFileName() + " REASON: " + e.getMessage());
            }
        }
        return true;
    }

    private Map<TypeElement, Handler> findTargets(RoundEnvironment env) {
        Map<TypeElement, Handler> map = new LinkedHashMap<TypeElement, Handler>();

        for (Element element : env.getElementsAnnotatedWith(FindView.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            Handler handler = map.get(enclosingElement);
            if (handler != null) {
                handler.addView(element);
            } else {
                handler = Handler.getHandler(element, elementUtil);
                map.put(enclosingElement, handler);
            }
            handler.addView(element);
        }
        return map;
    }

    static void debugLog(Object o) {
        if (o instanceof String || o instanceof CharSequence) {
            messager.printMessage(Diagnostic.Kind.NOTE, (CharSequence) o);
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, o.toString());
        }
    }

    static void errorLog(CharSequence cs) {
        messager.printMessage(Diagnostic.Kind.ERROR, cs);
    }
}
