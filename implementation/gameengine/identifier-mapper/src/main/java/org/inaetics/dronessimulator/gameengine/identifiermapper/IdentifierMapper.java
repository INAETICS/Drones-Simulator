package org.inaetics.dronessimulator.gameengine.identifiermapper;

import java.util.Optional;

public interface IdentifierMapper {
    Integer getNewGameEngineId();

    Optional<Integer> fromProtocolToGameEngineId(String id);
    Optional<String> fromGameEngineToProtocolId(Integer id);

    void setMapping(Integer id1, String id2);

    void removeMapping(Integer gameengineId);
    void removeMapping(String protocolId);
}
