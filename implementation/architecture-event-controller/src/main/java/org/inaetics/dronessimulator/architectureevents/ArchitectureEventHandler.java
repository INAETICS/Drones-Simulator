package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

/**
 * Functional interface for a function to be called upon a state transition.
 * The given arguments will consist of the new current lifecycle state
 */
@FunctionalInterface
public interface ArchitectureEventHandler {
    /**
     * How to handle the new transition
     * @param fromState The new from state
     * @param action The taken action
     * @param toState The new current state of the architecture
     */
    void handle(SimulationState fromState, SimulationAction action, SimulationState toState);
}
