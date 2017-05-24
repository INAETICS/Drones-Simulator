package org.inaetics.dronessimulator.discovery.api.discoverynode;

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

    public static DiscoveryPath type(String type) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type);
    }

    public static DiscoveryPath group(String type, String group) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type, group);
    }

    public static DiscoveryPath config(String type, String group, String name) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type, group, name);
    }

    public boolean isTypePath() {
        return this.getSegments().length == 2;
    }

    public boolean isGroupPath() {
        return this.getSegments().length == 3;
    }

    public boolean isConfigPath() {
        return this.getSegments().length == 4;
    }

    @Override
    protected DiscoveryPath newChild(String delimiter, String[] segments) {
        assert delimiter.equals(PATH_DELIMITER);

        return new DiscoveryPath(segments);
    }
}
