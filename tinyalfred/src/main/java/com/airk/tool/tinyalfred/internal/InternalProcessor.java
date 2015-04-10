package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.annotation.FindView;
import com.airk.tool.tinyalfred.annotation.OnClick;
import com.airk.tool.tinyalfred.annotation.OnLongClick;
import com.airk.tool.tinyalfred.annotation.OnPreDraw;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by kevin on 15/3/17.
 */
public final class InternalProcessor extends AbstractProcessor {

    Elements elementUtil;
    Filer filer;
    static Messager messager;
    Map<Class<? extends Annotation>, Handler> handlers = new LinkedHashMap<Class<? extends Annotation>, Handler>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtil = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

        initAllHandlers();
    }

    private void initAllHandlers() {
        handlers.put(FindView.class, new FindViewHandler());
        handlers.put(OnClick.class, new ClickHandler());
        handlers.put(OnLongClick.class, new LongClickHandler());
        handlers.put(OnPreDraw.class, new PreDrawHandler());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<String>();
        for (Map.Entry<Class<? extends Annotation>, Handler> entry : handlers.entrySet()) {
            set.add(entry.getValue().canHandle().getCanonicalName());
        }
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Map<TypeElement, Processor> processorMap = getAllProcessor(env);

        for (Map.Entry<TypeElement, Processor> entry : processorMap.entrySet()) {
            TypeElement element = entry.getKey();
            Processor processor = entry.getValue();
            try {
                JavaFileObject fileObject = filer.createSourceFile(processor.getFileName(), element);
                Writer writer = fileObject.openWriter();
                writer.write(processor.getGenerateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                errorLog("Can't write class " + processor.getFileName() + " REASON: " + e.getMessage());
            }
        }
        return true;
    }

    private Map<TypeElement, Processor> getAllProcessor(RoundEnvironment env) {
        Map<TypeElement, Processor> map = new LinkedHashMap<TypeElement, Processor>();

        for (Map.Entry<Class<? extends Annotation>, Handler> entry : handlers.entrySet()) {
            for (Element element : env.getElementsAnnotatedWith(entry.getKey())) {
                if (!modifierCheck(element, entry.getKey())) {
                    continue;
                }
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                Processor processor = map.get(enclosingElement);
                if (processor == null) {
                    processor = Processor.getProcessor(element, elementUtil, handlers);
                }
                entry.getValue().handle(processor, element);
                map.put(enclosingElement, processor);
            }
        }
        return map;
    }

    boolean modifierCheck(Element e, Class<? extends Annotation> clazz) {
        if (e.getModifiers().contains(Modifier.PRIVATE)) {
            errorLog("@" + clazz.getSimpleName() + " can\'t be used for private " + dumpElement(e)
                    + ", please keep it public or default.");
            return false;
        }
        return true;
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

    static String dumpElement(Element e) {
        if (e.getKind() == ElementKind.FIELD) {
            return "(" + e.asType() + ") " + e.getSimpleName() + " (" + e.getEnclosingElement() + ")";
        } else if (e.getKind() == ElementKind.METHOD) {
            StringBuilder sb = new StringBuilder();
            for (Modifier m : e.getModifiers()) {
                sb.append(m.toString()).append(" ");
            }
            sb.append(e.getSimpleName());
            ExecutableElement executableElement = (ExecutableElement) e;
            sb.append("(");
            for (VariableElement var : executableElement.getParameters()) {
                sb.append(" ").append(var.asType()).append(" ").append(var.getSimpleName()).append(",");
            }
            if (executableElement.getParameters() != null && executableElement.getParameters().size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(")");
            return sb.toString();
        } else {
            return "";
        }
    }
}
