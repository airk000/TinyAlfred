package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.TinyAlfred;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.*;

/**
 * Created by kevin on 15/3/17.
 */
final class Processor {

    private final String fullName;
    private final String packageName;
    private final String className;

    private Set<FindViewHandler.ViewBean> viewSet = new LinkedHashSet<FindViewHandler.ViewBean>();
    private Set<OnClickHandler.OnClickBean> clickSet = new LinkedHashSet<OnClickHandler.OnClickBean>();

    static Processor getProcessor(Element e, Elements util) {
        TypeElement enclosingElement = (TypeElement) e.getEnclosingElement();
        //com.airk.tool.alfred.simple.MyActivity.Holder
        String fullName = enclosingElement.getQualifiedName().toString();
        //com.airk.tool.alfred.simple
        String packageName = util.getPackageOf(enclosingElement).getQualifiedName().toString();
        //MyActivity$Holder
        String className = getClassName(enclosingElement, packageName) + TinyAlfred.SUFFIX;
        return new Processor(fullName, packageName, className);
    }

    private Processor(String fullName, String packageName, String className) {
        this.fullName = fullName;
        this.packageName = packageName;
        this.className = className;
    }

    void addFindView(FindViewHandler.ViewBean bean) {
        if (!viewSet.contains(bean)) {
            viewSet.add(bean);
        }
    }

    void addOnClick(OnClickHandler.OnClickBean bean) {
        if (!clickSet.contains(bean)) {
            clickSet.add(bean);
        }
    }

    String getGenerateJavaCode() {
        StringBuilder builder = new StringBuilder("//Generate from TinyAlfred at " + new Date(System.currentTimeMillis()).toString());
        builder.append("\npackage ").append(packageName).append(";\n\n");
        builder.append("import android.view.View;\n");
        builder.append("import android.app.Activity;\n");
        builder.append("import com.airk.tool.tinyalfred.TinyAlfred;\n");
        builder.append("import com.airk.tool.tinyalfred.Alfred;\n\n");

        builder.append("public class ").append(className)
                .append(" implements Alfred<").append(fullName).append(">").append(" {\n");
        builder.append("    @Override\n")
                .append("    public void handleViews(final ").append(fullName).append(" belong, Object root) {\n");

        for (FindViewHandler.ViewBean bean : viewSet) {
            builder.append("        belong.").append(bean.getName()).append(" = ").append("(").append(bean.getType()).append(") ")
                    .append(TinyAlfred.class.getSimpleName()).append(".findView(root, ").append(bean.getId())
                    .append(", \"").append(bean.getName()).append("\");\n");
        }

        builder.append("\n");
        for (OnClickHandler.OnClickBean bean : clickSet) {
            FindViewHandler.ViewBean viewBean = findViewInSet(bean.getTargetId());
            if (viewBean != null) { // this view has been founded
                builder.append("        belong.").append(viewBean.getName());
            } else {
                builder.append("        ").append(TinyAlfred.class.getSimpleName()).append(".findView(root, ")
                        .append(bean.getTargetId()).append(", \"").append(bean.getMethodName()).append("\")");
            }
            builder.append(".setOnClickListener(new View.OnClickListener() {\n")
                    .append("            @Override\n")
                    .append("            public void onClick(View view) {\n")
                    .append("                belong.").append(bean.getMethodName());
            if (bean.hasViewParam()) {
                builder.append("(view);\n");
            } else {
                builder.append("();\n");
            }
            builder.append("            }\n")
                    .append("        });\n");
        }

        builder.append("    }\n\n"); // method handleViews endpoint
        builder.append("}\n"); // class endpoint
        return builder.toString();
    }

    private FindViewHandler.ViewBean findViewInSet(int targetId) {
        for (FindViewHandler.ViewBean viewBean : viewSet) {
            if (viewBean.getId() == targetId) {
                return viewBean;
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
