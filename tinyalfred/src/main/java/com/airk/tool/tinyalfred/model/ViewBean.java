package com.airk.tool.tinyalfred.model;

/**
 * Created by kevin on 15/3/17.
 */
public class ViewBean {

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
