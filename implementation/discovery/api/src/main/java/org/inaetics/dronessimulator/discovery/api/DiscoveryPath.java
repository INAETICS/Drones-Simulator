package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.tree.Path;

/**
 * Configuration class for the discovery. Can build paths according the the configuration given the relevant parameters.
 */
public class DiscoveryPath extends Path<DiscoveryPath> {
    /** String representation of the root path for all paths. */
    public static final String ROOT = "";

    /** Root path as discovery path. */
    public static final DiscoveryPath ROOT_PATH = new DiscoveryPath(ROOT);

    /** Path delimiter. */
    public static final String PATH_DELIMITER = "/";

    /** Directory containing registered instances. */
    public static final String INSTANCE_DIR = "instances";

    /**
     * Builds a new instance given the path segments.
     * @param segments The path segments.
     */
    public DiscoveryPath(String... segments) {
        super(PATH_DELIMITER, segments);
    }

    /**
     * Builds and returns a new instance for a given discovery type.
     * @param type The discovery type.
     * @return A new discovery path instance for the given type.
     */
    public static DiscoveryPath type(Type type) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr());
    }

    /**
     * Builds and returns a new instance for a given discovery type and group.
     * @param type The discovery type.
     * @param group The discovery group for the given type.
     * @return A new discovery path instance for the given type and group.
     */
    public static DiscoveryPath group(Type type, Group group) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr(), group.getStr());
    }

    /**
     * Builds and returns a new instance for a given discovery config.
     * @param type The type of the config.
     * @param group The group of the config.
     * @param name The config name.
     * @return A new discovery path instance for the given config.
     */
    public static DiscoveryPath config(Type type, Group group, String name) {
        return new DiscoveryPath(ROOT, INSTANCE_DIR, type.getStr(), group.getStr(), name);
    }

    /**
     * @return Whether this path references a type node.
     */
    public boolean isTypePath() {
        return this.getSegments().length == 3;
    }

    /**
     * @return Whether this path references a group node under a type.
     */
    public boolean isGroupPath() {
        return this.getSegments().length == 4;
    }

    /**
     * @return Whether this path references a config leaf node under a group.
     */
    public boolean isConfigPath() {
        return this.getSegments().length == 5;
    }

    @Override
    protected DiscoveryPath newChild(String delimiter, String[] segments) {
        return new DiscoveryPath(segments);
    }
}
