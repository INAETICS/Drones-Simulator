package org.inaetics.dronessimulator.gameengine.identifiermapper;

import java.util.Optional;

/**
 * Identifier mapper service interface.
 */
public interface IdentifierMapper {
    /**
     * Generates and returns a new identifier for use in the game engine.
     * @return The new identifier.
     */
    Integer getNewGameEngineId();

    /**
     * Gets the game engine id corresponding to the given protocol id.
     * @param id The protocol id.
     * @return The game engine id.
     */
    Optional<Integer> fromProtocolToGameEngineId(String id);

    /**
     * Gets the protcol id corresponding to the given game engine id.
     * @param id The game engine id.
     * @return The protocol id.
     */
    Optional<String> fromGameEngineToProtocolId(Integer id);

    /**
     * Maps the given game engine id to the given protocol id and vice versa.
     * @param id1 The game engine id.
     * @param id2 The protocol id.
     */
    void setMapping(Integer id1, String id2);

    /**
     * Removes the mapping for the given game engine id.
     * @param gameengineId The game engine id.
     */
    void removeMapping(Integer gameengineId);

    /**
     * Removes the mapping for the given protocol id.
     * @param protocolId The protocol id.
     */
    void removeMapping(String protocolId);
}
