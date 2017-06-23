package org.inaetics.dronessimulator.discovery.api.discoverynode;

/**
 * Identifier for an instance type.
 *
 * This class provides a number of predefined types.
 */
public class Type {
    public static final Type DRONE = new Type("drone");
    public static final Type SERVICE = new Type("service");

    /** The string representation of this type. */
    private final String str;

    /**
     * Instantiates a new type with the given string representation.
     * @param str The string representation of this type.
     */
    public Type(String str) {
        this.str = str;
    }

    /**
     * Returns the string representation of this type.
     * @return The string representation of this type.
     */
    public String getStr() {
        return str;
    }
}
