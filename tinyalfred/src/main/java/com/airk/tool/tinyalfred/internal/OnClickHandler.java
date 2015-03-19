package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.annotation.OnClick;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

import android.view.View;

/**
 * Created by kevin on 15/3/19.
 */
public class OnClickHandler implements Handler {
    @Override
    public Class<? extends Annotation> canHandle() {
        return OnClick.class;
    }

    @Override
    public void handle(Processor processor, Element e) {
        int id = e.getAnnotation(OnClick.class).value();
        if (id == View.NO_ID) {
            InternalProcessor.errorLog("@OnClick must have a valid id like R.id.text1 (" + InternalProcessor.dumpElement(e) + ")");
            return;
        }
        if (!(e instanceof ExecutableElement)) {
            InternalProcessor.errorLog("@OnClick must be used at method, now is " + InternalProcessor.dumpElement(e));
            return;
        }
        ExecutableElement executableElement = (ExecutableElement) e;
        if (!executableElement.getReturnType().toString().equals("void")) {
            InternalProcessor.errorLog("Method " + InternalProcessor.dumpElement(e) + " return " + executableElement.getReturnType() + ","
                    + " it's no mean, please change it to 'void'.");
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
        OnClickBean bean = new OnClickBean(id, e.getSimpleName().toString(), hasOneViewParameter);
        processor.addOnClick(bean);
    }

    public class OnClickBean {
        private final int targetId;
        private final String methodName;
        private final boolean hasViewParam;

        public OnClickBean(int targetId, String methodName, boolean hasViewParam) {
            this.targetId = targetId;
            this.methodName = methodName;
            this.hasViewParam = hasViewParam;
        }

        public int getTargetId() {
            return targetId;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean hasViewParam() {
            return hasViewParam;
        }
    }
}
