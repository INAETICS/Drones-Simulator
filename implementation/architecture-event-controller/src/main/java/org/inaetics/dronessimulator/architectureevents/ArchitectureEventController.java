package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

public interface ArchitectureEventController {
    ArchitectureEventController addHandler(SimulationState fromState, SimulationAction action, SimulationState toState, ArchitectureEventHandler handler);
}
