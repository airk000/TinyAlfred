package com.airk.tool.tinyalfred.internal;

import com.airk.tool.tinyalfred.annotation.FindView;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

import android.view.View;

/**
 * Created by kevin on 15/3/19.
 */
class FindViewHandler implements Handler {
    @Override
    public Class<? extends Annotation> canHandle() {
        return FindView.class;
    }

    @Override
    public void handle(Processor processor, Element e) {
        if (!check(e)) {
            return;
        }
        int id = e.getAnnotation(FindView.class).value();
        if (id == View.NO_ID) {
            InternalProcessor.errorLog("@FindView must have a valid id like R.id.text1 (" + InternalProcessor.dumpElement(e) + ")");
            return;
        }
        FindViewHandler.ViewBean bean = new FindViewHandler.ViewBean(id, e.getSimpleName().toString(), e.asType().toString());
        processor.addFindView(bean);
    }

    private boolean check(Element e) {
        if (e.getKind() != ElementKind.FIELD) {
            InternalProcessor.errorLog("@FindView can only be used for field, now is " + InternalProcessor.dumpElement(e));
            return false;
        }
        if (!isSubtypeOfType(e.asType(), "android.view.View")) {
            InternalProcessor.errorLog("@FindView can only be used for View object, now is " + InternalProcessor.dumpElement(e));
            return false;
        }
        return true;
    }

    //from ButterKnife by JakeWharton
    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Created by kevin on 15/3/17.
     */
    static class ViewBean {

        private final int id;
        private final String name;
        private final String type;

        public ViewBean(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
