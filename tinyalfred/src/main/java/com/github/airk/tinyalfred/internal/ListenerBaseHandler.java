package com.github.airk.tinyalfred.internal;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

import android.view.View;
import com.github.airk.tinyalfred.TinyAlfred;
import com.github.airk.tinyalfred.annotation.ListenerDeclare;
import com.github.airk.tinyalfred.annotation.NullableView;

/**
 * Created by kevin on 15/4/15.
 */
abstract class ListenerBaseHandler implements Handler {
    @Override
    public void handle(Processor processor, Element e) {
        String annotationName = canHandle().getSimpleName();

        //id check begin
        int[] ids = getIds(e);
        if (ids == null || ids.length == 0) {
            InternalProcessor.errorLog(String.format("@%s must have at least one valid id like R.id.text1 (%s)", annotationName, InternalProcessor.dumpElement(e)));
            return;
        }
        for (int id : ids) {
            if (id == View.NO_ID) {
                InternalProcessor.errorLog(String.format("@%s must have a valid id like R.id.text1 (%s)", annotationName, InternalProcessor.dumpElement(e)));
                return;
            }
        }
        //id check end

        ExecutableElement executableElement = (ExecutableElement) e;
        ListenerDeclare declare = canHandle().getAnnotation(ListenerDeclare.class);
        if (!executableElement.getReturnType().toString().equals(declare.userReturnType())) {
            InternalProcessor.errorLog("Method " + InternalProcessor.dumpElement(e) + " return " + executableElement.getReturnType() + ","
                    + " it doesn't make sense, please change it to '" + declare.userReturnType() + "'.");
            return;
        }

        // one parameter View type check begin
        List<? extends VariableElement> params = executableElement.getParameters();
        if (!declare.hasViewParam() && (params != null && params.size() == 1)) {
            InternalProcessor.errorLog(String.format("Method %s not accept any parameter, please fix it on %s",
                    declare.listenerName(), InternalProcessor.dumpElement(e)));
            return;
        }
        if (params != null && params.size() > 1) {
            InternalProcessor.errorLog("Method " + InternalProcessor.dumpElement(e) + " has more than one parameter," +
                    " we can accept just one (and it must be View type) or none at all.");
            return;
        }
        boolean hasOneViewParameter = false;
        if (params != null && params.size() == 1) {
            VariableElement variableElement = params.get(0);
            if (!variableElement.asType().toString().equals("android.view.View")) {
                InternalProcessor.errorLog(String.format("@%s can accept just one method parameter, and it must be android.view.View type, now it's %s",
                        annotationName, variableElement.asType()));
                return;
            } else {
                hasOneViewParameter = true;
            }
        }
        ///end
        boolean nullCheck = e.getAnnotation(NullableView.class) != null;
        ListenerModel model = new ListenerModel(ids, e.getSimpleName().toString(), hasOneViewParameter, nullCheck, getType());
        processor.addListener(model);
    }

    @Override
    public String generateCode(Processor processor, String fullName) {
        StringBuilder builder = new StringBuilder("    @Override\n");
        builder.append("    public void ").append(getInterfaceName()).append("(final ").append(fullName).append(" belong, Object root) {\n");
        builder.append("        View tmp;\n");
        for (ListenerModel listener : processor.listenerSet) {
            if (listener.getType() != getType()) {
                continue;
            }
            for (int id : listener.getTargetId()) {
                boolean nullCheck;
                String hintName;
                ViewModel viewModel = Processor.findViewInSet(id, processor.viewSet);
                String varName;
                if (viewModel != null) { // this view has been founded
                    nullCheck = viewModel.isNullable();
                    hintName = viewModel.getName();
                    varName = hintName;
                    builder.append("        final View ").append(varName).append(" = belong.").append(viewModel.getName()).append(";\n");
                } else {
                    nullCheck = listener.isNullCheck();
                    hintName = String.valueOf(id);
                    varName = listener.getMethodName() + hintName;
                    builder.append("        final View ").append(varName).append(" = ").append(TinyAlfred.class.getSimpleName()).append(".findView(root, ")
                            .append(id).append(", \"").append(listener.getMethodName()).append("\");\n");
                }
                if (nullCheck) {
                    builder.append("        if (").append(varName).append(" == null) {\n")
                            .append("            throw new NullPointerException(\"View ").append(hintName)
                            .append(" can not be found, try @NullableView before if it is possible null, otherwise you have to find out why.\");\n")
                            .append("        }\n");
                }
                ListenerDeclare declare = canHandle().getAnnotation(ListenerDeclare.class);
                builder.append("        ").append(varName).append(".").append(declare.setterName()).append("(new ").append(declare.setterParam()).append(" {\n")
                        .append("            @Override\n")
                        .append("            public ").append(declare.listenerReturnType()).append(" ").append(declare.listenerName());
                if (declare.hasViewParam()) {
                    builder.append("(View view) {\n");
                } else {
                    builder.append("() {\n");
                }
                if (!declare.userReturnType().equals("void")) {
                    builder.append("                return ");
                } else {
                    builder.append("                ");
                }
                builder.append("belong.").append(listener.getMethodName());
                if (declare.hasViewParam() && listener.hasViewParam()) {
                    builder.append("(view);\n");
                } else {
                    builder.append("();\n");
                }
                String clean = cleanHook(varName);
                if (clean != null && !clean.isEmpty()) {
                    builder.append(clean);
                }
                builder.append("            }\n");
                builder.append("        });\n");
            }
        }
        builder.append("    }\n\n");
        return builder.toString();
    }

    protected abstract String cleanHook(String var);

    protected abstract int[] getIds(Element e);

    protected abstract ListenerModel.Type getType();

    protected abstract String getInterfaceName();
}
