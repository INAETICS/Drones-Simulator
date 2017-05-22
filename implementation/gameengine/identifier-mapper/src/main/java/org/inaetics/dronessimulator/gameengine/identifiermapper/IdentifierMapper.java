package org.inaetics.dronessimulator.gameengine.identifiermapper;

public interface IdentifierMapper {
    Integer getNewGameEngineId();

    Integer fromProtocolToGameEngineId(String id);
    String fromGameEngineToProtocolId(Integer id);

    void setMapping(Integer id1, String id2);

    void removeMapping(Integer gameengineId);
    void removeMapping(String protocolId);
}
