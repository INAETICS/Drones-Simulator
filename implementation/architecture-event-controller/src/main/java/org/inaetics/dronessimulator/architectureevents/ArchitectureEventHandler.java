package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

public interface ArchitectureEventHandler {
    void handle(SimulationState fromState, SimulationAction action, SimulationState toState);
}
