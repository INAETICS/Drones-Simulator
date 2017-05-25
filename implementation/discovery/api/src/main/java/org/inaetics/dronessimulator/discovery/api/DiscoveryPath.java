package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.tree.Path;

public class DiscoveryPath extends Path<DiscoveryPath> {
    public static final String ROOT = "";
    public static final DiscoveryPath ROOT_PATH = new DiscoveryPath(ROOT);
    public static final String PATH_DELIMITER = "/";

    public static final String INSTANCE_DIR = "instances";

    public static final String DRONES = "drones";

    public DiscoveryPath(String... segments) {
        super(PATH_DELIMITER, segments);
    }

    public static DiscoveryPath type(Type type) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr());
    }

    public static DiscoveryPath group(Type type, Group group) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr(), group.getStr());
    }

    public static DiscoveryPath config(Type type, Group group, String name) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr(), group.getStr(), name);
    }

    public boolean isTypePath() {
        return this.getSegments().length == 3;
    }

    public boolean isGroupPath() {
        return this.getSegments().length == 4;
    }

    public boolean isConfigPath() {
        return this.getSegments().length == 5;
    }

    @Override
    protected DiscoveryPath newChild(String delimiter, String[] segments) {
        assert delimiter.equals(PATH_DELIMITER);

        return new DiscoveryPath(segments);
    }
}
