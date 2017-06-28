package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

public class LifeCycleStep {
    private final SimulationState fromState;
    private final SimulationAction action;
    private final SimulationState toState;

    public LifeCycleStep(SimulationState fromState, SimulationAction action, SimulationState toState) {
        this.fromState = fromState;
        this.action = action;
        this.toState = toState;
    }


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
