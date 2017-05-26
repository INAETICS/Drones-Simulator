package org.inaetics.dronessimulator.discovery.api.discoverynode;

public class Type {
    public static final Type DRONE = new Type("drone");
    public static final Type SERVICE = new Type("service");

    private final String str;

    private Type(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
