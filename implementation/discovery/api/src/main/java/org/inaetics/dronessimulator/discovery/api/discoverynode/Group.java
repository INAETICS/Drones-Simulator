package org.inaetics.dronessimulator.discovery.api.discoverynode;

/**
 * Identifier for an instance group. Groups are one level below types.
 *
 * This class provides a number of predefined groups.
 */
public class Group {
    public static final Group DRONE = new Group("drone");
    public static final Group SERVICES = new Group("services");

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