package com.airk.tool.tinyalfred.internal;

import android.view.View;
import com.airk.tool.tinyalfred.TinyAlfred;
import com.airk.tool.tinyalfred.annotation.ListenerDeclare;
import com.airk.tool.tinyalfred.annotation.NullableView;
import com.airk.tool.tinyalfred.annotation.OnClick;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by kevin on 15/3/19.
 */
class ClickHandler implements Handler {
    @Override
    public Class<? extends Annotation> canHandle() {
        return OnClick.class;
    }

    @Override
    public void handle(Processor processor, Element e) {
        int[] ids = e.getAnnotation(OnClick.class).value();
        if (ids == null || ids.length == 0) {
            InternalProcessor.errorLog("@OnClick must have at least one valid id like R.id.text1 (" + InternalProcessor.dumpElement(e) + ")");
            return;
        }
        for (int id : ids) {
            if (id == View.NO_ID) {
                InternalProcessor.errorLog("@OnClick must have a valid id like R.id.text1 (" + InternalProcessor.dumpElement(e) + ")");
                return;
            }
        }
        if (!(e instanceof ExecutableElement)) {
            InternalProcessor.errorLog("@OnClick must be used at method, now is " + InternalProcessor.dumpElement(e));
            return;
        }
        ExecutableElement executableElement = (ExecutableElement) e;
        if (!executableElement.getReturnType().toString().equals("void")) {
            InternalProcessor.errorLog("Method " + InternalProcessor.dumpElement(e) + " return " + executableElement.getReturnType() + ","
                    + " it doesn't make sense, please change it to 'void'.");
            return;
        }
        List<? extends VariableElement> params = executableElement.getParameters();
        if (params != null && params.size() > 1) {
            InternalProcessor.errorLog("Method " + InternalProcessor.dumpElement(e) + " has more than one parameter," +
                    " we can accept just one (and it must be View type) or none at all.");
            return;
        }

        boolean hasOneViewParameter = false;
        if (params != null && params.size() == 1) {
            VariableElement variableElement = params.get(0);
            if (!variableElement.asType().toString().equals("android.view.View")) {
                InternalProcessor.errorLog("@OnClick can accept just one method parameter, and it must be android.view.View type, now it's "
                        + variableElement.asType());
                return;
            } else {
                hasOneViewParameter = true;
            }
        }
        boolean nullCheck = e.getAnnotation(NullableView.class) != null;
        OnClickModel model = new OnClickModel(ids, e.getSimpleName().toString(), hasOneViewParameter, nullCheck);
        processor.addOnClick(model);
    }

    @Override
    public String generateCode(Processor processor, String fullName) {
        StringBuilder builder = new StringBuilder("    @Override\n");
        builder.append("    public void handleListeners(final ").append(fullName).append(" belong, Object root) {\n");
        builder.append("        View tmp;\n");
        for (OnClickModel model : processor.clickSet) {
            for (int id : model.getTargetId()) {
                boolean nullCheck;
                String hintName;
                ViewModel viewModel = Processor.findViewInSet(id, processor.viewSet);
                if (viewModel != null) { // this view has been founded
                    nullCheck = viewModel.isNullable();
                    hintName = viewModel.getName();
                    builder.append("        tmp = belong.").append(viewModel.getName()).append(";\n");
                } else {
                    nullCheck = model.isNullCheck();
                    hintName = String.valueOf(id);
                    builder.append("        tmp = ").append(TinyAlfred.class.getSimpleName()).append(".findView(root, ")
                            .append(id).append(", \"").append(model.getMethodName()).append("\");\n");
                }
                if (nullCheck) {
                    builder.append("        if (tmp == null) {\n")
                            .append("            throw new NullPointerException(\"View ").append(hintName)
                            .append(" can not be found, try @NullableView before if it is possible null, otherwise you have to find out why.\");\n")
                            .append("        }\n");
                }
                ListenerDeclare declare = OnClick.class.getAnnotation(ListenerDeclare.class);
                builder.append("        tmp.").append(declare.setterName()).append("(new ").append(declare.setterParam()).append(" {\n")
                        .append("            @Override\n")
                        .append("            public void ").append(declare.listenerName())
                        .append("(View view) {\n");
                builder.append("                belong.").append(model.getMethodName());

                if (model.hasViewParam()) {
                    builder.append("(view);\n");
                } else {
                    builder.append("();\n");
                }
                builder.append("            }\n");
                builder.append("        });\n");
            }
        }
        builder.append("    }\n\n");
        return builder.toString();
    }

}
