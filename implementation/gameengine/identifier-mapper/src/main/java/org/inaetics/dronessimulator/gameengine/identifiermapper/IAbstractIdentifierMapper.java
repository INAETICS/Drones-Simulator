package org.inaetics.dronessimulator.gameengine.identifiermapper;

/**
 * Interface for identifier mappers. Identifier mappers map identifiers in different systems which refer to the same
 * real-world thing.
 * @param <I1> The type of identifiers from the first system.
 * @param <I2> The type of identifiers from the second system.
 */
public interface IAbstractIdentifierMapper<I1, I2> {
    /**
     * Gets the system two identifier given the corresponding system one identifier.
     * @param id The system one identifier.
     * @return The corresponding system two identifier.
     */
    I2 fromOneToTwo(I1 id);

    /**
     * Gets the system one identifier given the corresponding system two identifier.
     * @param id The system two identifier.
     * @return The corresponding system one identifier.
     */
    I1 fromTwoToOne(I2 id);

    /**
     * Declare the given identifiers to refer to the same object in the two systems. Mappings are commutative,
     * meaning that id1 -> id2 implies id2 -> id1.
     * @param id1 The identifier in the first system.
     * @param id2 The identifier in the second system.
     */
    void setMapping(I1 id1, I2 id2);

    /**
     * Removes a mapping of two identifiers.
     * @param id1 The identifier in the first system.
     * @param id2 The identifier in the second system.
     */
    void removeMapping(I1 id1, I2 id2);
}
