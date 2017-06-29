package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

/**
 * Datastructure to combine the current state into a single value
 * This allows handlers to be related to a specific LifeCycleStep key in maps
 * Also allows life cycle steps to be easily compared
 */
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

    /**
     * Create a LifeCycleStep
     * @param fromState The from state
     * @param action The taken action
     * @param toState The new current state
     */
    public LifeCycleStep(SimulationState fromState, SimulationAction action, SimulationState toState) {
        this.fromState = fromState;
        this.action = action;
        this.toState = toState;
    }

    @Override
    public int hashCode() {
        return fromState.hashCode() + action.hashCode() + toState.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof LifeCycleStep) {
            LifeCycleStep o = (LifeCycleStep) other;
            return fromState.equals(o.fromState) && action.equals(o.action) && toState.equals(o.toState);
        } else {
            return false;
        }
    }
}
