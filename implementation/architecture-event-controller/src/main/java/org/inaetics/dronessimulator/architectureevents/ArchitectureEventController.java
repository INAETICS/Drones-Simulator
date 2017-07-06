package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;

/**
 * Service interface for ArchitectureEventControllerServer. You can add handlers which are called when the architecture
 * makes that transition. A transition is based upon a from state, taken action and new state. Say the architecture
 * receives the pause action while the simulation is running. The transition in this example is: (RUNNING, PAUSE, PAUSED)
 */
@FunctionalInterface
public interface ArchitectureEventController {
    /**
     * Add an handler which is called upon a certain lifecycle step
     * @param fromState The old state in the lifecycle step
     * @param action The action performed in the lifecycle step
     * @param toState The new current state in the lifecycle step. The architecture will be at this state
     * @param handler The handler to perform when the lifecycle step is taken
     * @return The this controller object to allow for fluid calling
     */
    ArchitectureEventController addHandler(SimulationState fromState, SimulationAction action, SimulationState toState, ArchitectureEventHandler handler);
}
