package org.inaetics.dronessimulator.gameengine.identifiermapper;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class IdentifierMapperService extends AbstractIdentifierMapper<Integer, String> implements IdentifierMapper {
    private final AtomicInteger nextGameEngineId;

    public IdentifierMapperService() {
        nextGameEngineId = new AtomicInteger(0);
    }

    @Override
    public Integer getNewGameEngineId() {
        return this.nextGameEngineId.incrementAndGet();
    }

    @Override
    public Optional<Integer> fromProtocolToGameEngineId(String id) {
        return Optional.ofNullable(fromTwoToOne(id));
    }

    @Override
    public Optional<String> fromGameEngineToProtocolId(Integer id) {
        return Optional.ofNullable(fromOneToTwo(id));
    }

    @Override
    public void removeMapping(Integer gameengineId) {
        Optional<String> maybeProtocolId = this.fromGameEngineToProtocolId(gameengineId);

        if(maybeProtocolId.isPresent()) {
            this.removeMapping(gameengineId, maybeProtocolId.get());
        }
    }

    @Override
    public void removeMapping(String protocolId) {
        Optional<Integer> maybeGameengineId = this.fromProtocolToGameEngineId(protocolId);

        if(maybeGameengineId.isPresent()) {
            this.removeMapping(maybeGameengineId.get(), protocolId);
        }
    }


}
