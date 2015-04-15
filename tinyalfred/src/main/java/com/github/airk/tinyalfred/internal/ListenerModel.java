package com.github.airk.tinyalfred.internal;

/**
 * Created by kevin on 15/4/10.
 */
final class ListenerModel {
    private final int[] targetId;
    private final String methodName;
    private final boolean hasViewParam;
    private final boolean nullCheck;
    private final Type type;

    enum Type {
        CLICK,
        LONG_CLICK,
        PRE_DRAW
    }

    public ListenerModel(int[] targetId, String methodName, boolean hasViewParam, boolean nullCheck, Type type) {
        this.targetId = targetId;
        this.methodName = methodName;
        this.hasViewParam = hasViewParam;
        this.nullCheck = nullCheck;
        this.type = type;
    }

    public int[] getTargetId() {
        return targetId;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean hasViewParam() {
        return hasViewParam;
    }

    public boolean isNullCheck() {
        return nullCheck;
    }

    public Type getType() {
        return type;
    }
}
