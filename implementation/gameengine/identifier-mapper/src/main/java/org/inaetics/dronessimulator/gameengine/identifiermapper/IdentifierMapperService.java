package org.inaetics.dronessimulator.gameengine.identifiermapper;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class IdentifierMapperService extends AbstractIdentifierMapper<Integer, String> implements IdentifierMapper {
    private volatile ArchitectureEventController m_architectureEventController;

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

    public void start() {
        m_architectureEventController.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            this.oneToTwo.clear();
            this.twoToOne.clear();
        });
    }
}
