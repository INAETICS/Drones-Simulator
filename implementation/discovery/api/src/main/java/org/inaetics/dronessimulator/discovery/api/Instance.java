package org.inaetics.dronessimulator.discovery.api;

import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Interface which describes a part of a system.
 */
public class Instance {
    /**
     * Prefix for all etcd paths.
     */
    private static final String PATH_PREFIX = "/";
    /**
     * Prefix/location for instance references.
     */
    private static final String INSTANCE_DIR = "instances";

    /**
     * The type of this instance.
     */
    private final Type type;

    public Type getType() {
        return type;
    }

    /**
     * The group of this instance.
     */
    private final Group group;

    public Group getGroup() {
        return group;
    }

    /**
     * The name of this instance.
     */
    private final String name;

    public String getName() {
        return name;
    }

    /**
     * The properties this instance has.
     */
    private final Map<String, String> properties;

    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Instantiates a new instance with the given type, group, name and properties. This constructor can be used to
     * build a quick instance. However, it is recommended to subclass this class and use your own constructor together
     * with overriding the setInitialProperties method.
     *
     * @param type       The type of this instance.
     * @param group      The group of this instance.
     * @param name       The name of this instance.
     * @param properties The properties of this instance.
     */
    public Instance(Type type, Group group, String name, Map<String, String> properties) {
        this.type = type;
        this.group = group;
        this.name = name;
        this.properties = properties == null ? new HashMap<>() : properties;

        this.setInitialProperties(properties);
    }

    /**
     * Instantiates a new instance with the given type, group, name and properties.
     *
     * @param type  The type of this instance.
     * @param group The group of this instance.
     * @param name  The name of this instance.
     */
    public Instance(Type type, Group group, String name) {
        this(type, group, name, null);
    }

    /**
     * Builds an etcd path from a number of strings.
     *
     * @param segments The segments of the path.
     * @return The constructed path.
     */
    public static String buildPath(String... segments) {
        return PATH_PREFIX + String.join("/", segments);
    }

    /**
     * Builds an etcd path for the given instance.
     *
     * @param instance The instance to build the path for.
     * @return The path for the instance.
     */
    public static String buildInstancePath(Instance instance) {
        return buildInstancePath(instance.getType(), instance.getGroup(), instance.getName());
    }

    /**
     * Builds an etcd path for the given parameters.
     *
     * @param type  The type of the instance.
     * @param group The group of the instance.
     * @param name  The name of the instance.
     * @return The path for the instance.
     */
    static String buildInstancePath(Type type, Group group, String name) {
        return buildInstancePath(type.getStr(), group.getStr(), name);
    }

    /**
     * Builds an etcd path for the given parameters.
     *
     * @param type  The type of the instance.
     * @param group The group of the instance.
     * @param name  The name of the instance.
     * @return The path for the instance.
     */
    static String buildInstancePath(String type, String group, String name) {
        return buildPath(INSTANCE_DIR, type, group, name);
    }

    /**
     * Sets the initial properties of this instance. This method is called from the constructor and only once. Override
     * this method to add your own properties to the given map.
     *
     * @param properties The properties of this instance.
     */
    protected void setInitialProperties(Map<String, String> properties) {
        // Left blank intentionally.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;
        Instance instance = (Instance) o;
        return Objects.equals(type, instance.type) &&
                Objects.equals(group, instance.group) &&
                Objects.equals(name, instance.name) &&
                Objects.equals(properties, instance.properties);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, group, name, properties);
    }
}
