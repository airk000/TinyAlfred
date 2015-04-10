package com.airk.tool.tinyalfred.internal;

/**
 * Created by kevin on 15/4/10.
 */
public class ViewModel {
    private final int id;
    private final String name;
    private final String type;
    private final boolean nullable;

    public ViewModel(int id, String name, String type, boolean nullable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.nullable = nullable;
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

    public boolean isNullable() {
        return nullable;
    }
}
