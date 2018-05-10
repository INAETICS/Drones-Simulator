package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

import java.util.Objects;

/**
 * Datastructure to combine the current state into a single value
 * This allows handlers to be related to a specific LifeCycleStep key in maps
 * Also allows life cycle steps to be easily compared
 */

public class LifeCycleStep {
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

    public LifeCycleStep(SimulationState fromState, SimulationAction action, SimulationState toState) {
        this.fromState = fromState;
        this.action = action;
        this.toState = toState;
    }

    @Override
    public String toString() {
        return "LifeCycleStep{" +
                "fromState=" + fromState +
                ", action=" + action +
                ", toState=" + toState +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LifeCycleStep)) return false;
        LifeCycleStep that = (LifeCycleStep) o;
        return fromState == that.fromState &&
                action == that.action &&
                toState == that.toState;
    }

    @Override
    public int hashCode() {

        return Objects.hash(fromState, action, toState);
    }
}
