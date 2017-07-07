package org.inaetics.dronessimulator.discovery.api.discoverynode;

/**
 * Identifier for an instance group. Groups are one level below types.
 *
 * This class provides a number of predefined groups.
 * Is used with the discovery as the second part of the path
 */
public class Group {
    /** The Drone group */
    public static final Group DRONE = new Group("drone");
    /** The Services group */
    public static final Group SERVICES = new Group("services");
    /** The Broker group */
    public static final Group BROKER = new Group("broker");

    /** String representation of this group. */
    private final String str;

    /**
     * Instantiates a new group with the given string representation.
     * @param str String representation of this group.
     */
    public Group(String str) {
        this.str = str;
    }

    /**
     * Returns the string representation of this group.
     * @return The string representation of this group.
     */
    public String getStr() {
        return str;
    }
}