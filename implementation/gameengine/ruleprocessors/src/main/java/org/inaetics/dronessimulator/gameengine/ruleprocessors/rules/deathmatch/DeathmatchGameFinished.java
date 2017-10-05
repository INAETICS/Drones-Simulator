package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.AbstractGameFinishedRule;

import java.util.List;

public class DeathmatchGameFinished extends AbstractGameFinishedRule {
    private int winner;

    @Override
    protected String getWinner() {
        return String.valueOf(winner);
    }

    @Override
    protected boolean gameIsFinished(List<GameEntity> currentState) {
        final int[] count = {0}; //One element array to avoid the final constraint by lambdas
        currentState.stream().filter(gameEntity -> EntityType.DRONE.equals(gameEntity.getType())).map(gameEntity -> ((Drone) gameEntity)).filter(drone -> drone.getHp() > 0).forEach(drone -> {
            count[0]++;
            winner = drone.getEntityId();
        });
        return count[0] == 1;
    }
}
