package org.inaetics.dronessimulator.discovery.api.discoverynode;

public class Group {
    public static final Group DRONE = new Group("drone");
    public static final Group SERVICES = new Group("services");
    
    private final String str;
    
    private Group(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}