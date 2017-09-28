package org.inaetics.dronessimulator.architectureevents;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

/**
 * Datastructure to combine the current state into a single value
 * This allows handlers to be related to a specific LifeCycleStep key in maps
 * Also allows life cycle steps to be easily compared
 */
@EqualsAndHashCode
@AllArgsConstructor
@ToString
class LifeCycleStep {
    /**
     * The from state
     */
    private final SimulationState fromState;
    /**
     * The taken action
     */
    private final SimulationAction action;
    /**
     * The new current state
     */
    private final SimulationState toState;
}
