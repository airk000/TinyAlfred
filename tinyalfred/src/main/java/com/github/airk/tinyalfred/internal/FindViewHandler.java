package com.github.airk.tinyalfred.internal;

import android.view.View;
import com.github.airk.tinyalfred.TinyAlfred;
import com.github.airk.tinyalfred.annotation.FindView;
import com.github.airk.tinyalfred.annotation.NullableView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

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
        boolean nullable = e.getAnnotation(NullableView.class) != null;
        InternalProcessor.debugLog(nullable + "@" + InternalProcessor.dumpElement(e));
        ViewModel model = new ViewModel(id, e.getSimpleName().toString(), e.asType().toString(), nullable);
        processor.addFindView(model);
    }

    @Override
    public String generateCode(Processor processor, String fullName) {
        StringBuilder builder = new StringBuilder("    @Override\n");
        builder.append("    public void findViews(final ").append(fullName).append(" belong, Object root) {\n");
        for (ViewModel model : processor.viewSet) {
            builder.append("        belong.").append(model.getName()).append(" = ").append("(").append(model.getType()).append(") ")
                    .append(TinyAlfred.class.getSimpleName()).append(".findView(root, ").append(model.getId())
                    .append(", \"").append(model.getName()).append("\");\n");
            //do a null point check, if there is a null point bug, let user attention it here but not later.
            if (model.isNullable()) {
                builder.append("        if (belong.").append(model.getName()).append(" == null) {\n")
                        .append("            throw new NullPointerException(\"View ").append(model.getName())
                        .append(" can not be found, try @NullableView before if it is possible null, otherwise you have to find out why.\");\n")
                        .append("        }\n");
            }
        }
        builder.append("    }\n\n");
        return builder.toString();
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

}
