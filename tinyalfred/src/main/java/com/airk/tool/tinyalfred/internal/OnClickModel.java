package com.airk.tool.tinyalfred.internal;

/**
 * Created by kevin on 15/4/10.
 */
public class OnClickModel {
    private final int[] targetId;
    private final String methodName;
    private final boolean hasViewParam;
    private final boolean nullCheck;

    public OnClickModel(int[] targetId, String methodName, boolean hasViewParam, boolean nullCheck) {
        this.targetId = targetId;
        this.methodName = methodName;
        this.hasViewParam = hasViewParam;
        this.nullCheck = nullCheck;
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
}
