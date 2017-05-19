package org.inaetics.dronessimulator.gameengine.identifiermapper;

public interface IIdentifierMapper {
    Integer getNewGameEngineId();

    Integer fromProtocolToGameEngineId(String id);
    String fromGameEngineToProtocolId(Integer id);

    void setMapping(Integer id1, String id2);
}
