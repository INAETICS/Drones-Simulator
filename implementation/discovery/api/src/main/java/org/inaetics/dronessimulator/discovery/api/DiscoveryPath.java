package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryHandler;
import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class DiscoveryPath extends Path {
    public static final String PATH_DELIMITER = "/";
    public static final String ROOT = "/";
    public static final String INSTANCE_DIR = "instances";

    public static final String DRONES = "drones";

    private DiscoveryPath(String... segments) {
        super(PATH_DELIMITER, segments);
    }

    public static DiscoveryPath type(String type) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type);
    }

    public static DiscoveryPath group(String type, String group) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type, group);
    }

    public static DiscoveryPath config(String type, String group, String name) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type, group, name);
    }
}
