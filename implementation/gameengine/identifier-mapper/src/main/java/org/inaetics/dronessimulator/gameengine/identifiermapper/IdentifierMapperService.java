package org.inaetics.dronessimulator.gameengine.identifiermapper;

import java.util.concurrent.atomic.AtomicInteger;

public class IdentifierMapperService extends AbstractIdentifierMapper<Integer, String> implements IIdentifierMapper {
    private final AtomicInteger nextGameEngineId;

    public IdentifierMapperService() {
        nextGameEngineId = new AtomicInteger(0);
    }

    @Override
    public Integer getNewGameEngineId() {
        return this.nextGameEngineId.incrementAndGet();
    }

    @Override
    public Integer fromProtocolToGameEngineId(String id) {
        return fromTwoToOne(id);
    }

    @Override
    public String fromGameEngineToProtocolId(Integer id) {
        return fromOneToTwo(id);
    }


}
