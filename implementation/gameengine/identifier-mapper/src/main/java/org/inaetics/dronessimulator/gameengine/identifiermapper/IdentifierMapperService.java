package org.inaetics.dronessimulator.gameengine.identifiermapper;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OSGi service for the identifier mapper.
 */
public class IdentifierMapperService extends AbstractIdentifierMapper<Integer, String> implements IdentifierMapper {
    private volatile ArchitectureEventController m_architectureEventController;

    /** The next used game engine id. */
    private final AtomicInteger nextGameEngineId;

    /**
     * Instantiates a new identifier mapper service.
     */
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
        m_architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            this.oneToTwo.clear();
            this.twoToOne.clear();
        });
    }


}
