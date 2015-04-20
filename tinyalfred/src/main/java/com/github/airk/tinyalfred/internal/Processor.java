package com.github.airk.tinyalfred.internal;

import com.github.airk.tinyalfred.TinyAlfred;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kevin on 15/3/17.
 */
final class Processor {

    private final String fullName;
    private final String packageName;
    private final String className;
    private final Map<Class<? extends Annotation>, Handler> handlers;

    Set<ViewModel> viewSet = new LinkedHashSet<ViewModel>();
    Set<ListenerModel> listenerSet = new LinkedHashSet<ListenerModel>();

    static Processor getProcessor(Element e, Elements util, Map<Class<? extends Annotation>, Handler> handlers) {
        TypeElement enclosingElement = (TypeElement) e.getEnclosingElement();
        //com.airk.tool.alfred.simple.MyActivity.Holder
        String fullName = enclosingElement.getQualifiedName().toString();
        //com.airk.tool.alfred.simple
        String packageName = util.getPackageOf(enclosingElement).getQualifiedName().toString();
        //MyActivity$Holder
        String className = getClassName(enclosingElement, packageName) + TinyAlfred.SUFFIX;
        return new Processor(fullName, packageName, className, handlers);
    }

    private Processor(String fullName, String packageName, String className, Map<Class<? extends Annotation>, Handler> handlers) {
        this.fullName = fullName;
        this.packageName = packageName;
        this.className = className;
        this.handlers = handlers;
    }

    void addFindView(ViewModel v) {
        if (!viewSet.contains(v)) {
            viewSet.add(v);
        }
    }

    void addListener(ListenerModel l) {
        if (!listenerSet.contains(l)) {
            listenerSet.add(l);
        }
    }

    String getGenerateJavaCode() {
        StringBuilder builder = new StringBuilder("//Generate from TinyAlfred at " + new Date(System.currentTimeMillis()).toString());
        builder.append("\npackage ").append(packageName).append(";\n\n");
        builder.append("import android.view.View;\n");
        builder.append("import android.app.Activity;\n");
        builder.append("import com.github.airk.tinyalfred.TinyAlfred;\n");
        builder.append("import com.github.airk.tinyalfred.Alfred;\n\n");

        builder.append("public class ").append(className)
                .append(" implements Alfred<").append(fullName).append(">").append(" {\n");
        for (Map.Entry<Class<? extends Annotation>, Handler> e : handlers.entrySet()) {
            Handler h = e.getValue();
            builder.append(h.generateCode(this, fullName));
        }
        builder.append("}\n"); // class endpoint
        return builder.toString();
    }

    static ViewModel findViewInSet(int targetId, Set<ViewModel> viewSet) {
        for (ViewModel m : viewSet) {
            if (m.getId() == targetId) {
                return m;
            }
        }
        return null;
    }

    String getFileName() {
        return packageName + "." + className;
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
