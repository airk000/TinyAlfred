package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.TinyAlfred;
import com.airk.tool.tinyalfred.annotation.FindView;
import com.airk.tool.tinyalfred.model.ViewBean;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.*;

/**
 * Created by kevin on 15/3/17.
 */
final class Handler {

    private final String fullName;
    private final String packageName;
    private final String className;

    private Set<ViewBean> viewSet = new LinkedHashSet<ViewBean>();

    static Handler getHandler(Element e, Elements util) {
        TypeElement enclosingElement = (TypeElement) e.getEnclosingElement();
        //com.airk.tool.alfred.simple.MyActivity.Holder
        String fullName = enclosingElement.getQualifiedName().toString();
        //com.airk.tool.alfred.simple
        String packageName = util.getPackageOf(enclosingElement).getQualifiedName().toString();
        //MyActivity$Holder
        String className = getClassName(enclosingElement, packageName) + TinyAlfred.SUFFIX;
        return new Handler(enclosingElement, fullName, packageName, className);
    }

    private Handler(Element enclosingElement, String fullName, String packageName, String className) {
        this.fullName = fullName;
        this.packageName = packageName;
        this.className = className;
    }

    void addView(Element e) {
        int id = e.getAnnotation(FindView.class).value();
        ViewBean bean = new ViewBean(id, e.getSimpleName().toString(), e.asType().toString());
        if (!viewSet.contains(bean)) {
            viewSet.add(bean);
        }
    }

    String getGenerateJavaCode() {
        StringBuilder builder = new StringBuilder("//Generate from TinyAlfred at " + new Date(System.currentTimeMillis()).toString());
        builder.append("\npackage ").append(packageName).append(";\n\n");
        builder.append("import android.view.View;\n");
        builder.append("import android.app.Activity;\n");
        builder.append("import com.airk.tool.tinyalfred.Alfred;\n\n");

        builder.append("public class ").append(className)
                .append(" implements Alfred").append(" {\n");
        builder.append("    @Override\n")
                .append("    public void handleViews(Object belong, Object root) {\n");

        builder.append("        if (root instanceof Activity) {\n");
        for (ViewBean bean : viewSet) {
            builder.append("            ((").append(fullName).append(") belong).").append(bean.getName()).append(" = ").append("(").append(bean.getType()).append(") ")
                    .append("((Activity) root).findViewById(").append(bean.getId()).append(");\n");
        }
        builder.append("        } else if (root instanceof View) {\n");
        for (ViewBean bean : viewSet) {
            builder.append("            ((").append(fullName).append(") belong).").append(bean.getName()).append(" = ").append("(").append(bean.getType()).append(") ")
                    .append("((View) root).findViewById(").append(bean.getId()).append(");\n");
        }
        builder.append("        }\n");
        builder.append("    }\n\n");
        builder.append("}\n");
        return builder.toString();
    }

    String getFileName() {
        return packageName + "." + className;
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
