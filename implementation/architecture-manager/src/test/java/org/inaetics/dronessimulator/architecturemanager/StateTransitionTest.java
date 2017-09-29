package org.inaetics.dronessimulator.architecturemanager;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateTransitionTest {
    SimulationState[] validCurrentStates = new SimulationState[]{
            SimulationState.NOSTATE,
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.CONFIG,
            SimulationState.RUNNING,
            SimulationState.RUNNING,
            SimulationState.RUNNING,
            SimulationState.PAUSED,
            SimulationState.PAUSED,
            SimulationState.DONE,
    };
    SimulationAction[] validActions = new SimulationAction[]{
            SimulationAction.INIT,
            SimulationAction.CONFIG,
            SimulationAction.STOP,
            SimulationAction.START,
            SimulationAction.STOP,
            SimulationAction.PAUSE,
            SimulationAction.GAMEOVER,
            SimulationAction.STOP,
            SimulationAction.RESUME,
            SimulationAction.STOP,
    };
    SimulationState[] validFinalStates = new SimulationState[]{
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.INIT,
            SimulationState.RUNNING,
            SimulationState.INIT,
            SimulationState.PAUSED,
            SimulationState.DONE,
            SimulationState.INIT,
            SimulationState.RUNNING,
            SimulationState.INIT,
    };

    int numActions = 10;

    SimulationState[] allStates = new SimulationState[]{
            SimulationState.NOSTATE,
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.RUNNING,
            SimulationState.PAUSED,
            SimulationState.DONE,
    };
    SimulationAction[] allActions = new SimulationAction[]{
            SimulationAction.INIT,
            SimulationAction.CONFIG,
            SimulationAction.START,
            SimulationAction.PAUSE,
            SimulationAction.RESUME,
            SimulationAction.GAMEOVER,
            SimulationAction.STOP,
    };

    @Test
    public void testNextState() throws Exception {
        for (SimulationState startState : allStates) {
            for (SimulationAction action : allActions) {
                SimulationState endState = ArchitectureManager.nextState(startState, action);

                for (int i = 0; i < numActions; i++) {
                    if (startState == validCurrentStates[i] && action == validActions[i]) {
                        String msg = String.format("%s.%s", startState.toString(), action.toString());
                        assertEquals(msg, validFinalStates[i], endState);
                    }
                }
            }
        }
    }

}
