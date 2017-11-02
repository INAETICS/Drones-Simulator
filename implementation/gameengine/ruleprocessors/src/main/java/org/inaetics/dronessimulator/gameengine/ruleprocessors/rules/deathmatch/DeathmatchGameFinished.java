package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.AbstractGameFinishedRule;

import java.util.List;
import java.util.Optional;

public class DeathmatchGameFinished extends AbstractGameFinishedRule {
    private int winner;

    public DeathmatchGameFinished(IdentifierMapper idMapper) {
        super(idMapper);
    }

    @Override
    protected String getWinner() {
        String droneName = String.valueOf(winner);
        Optional<String> droneId = idMapper.fromGameEngineToProtocolId(winner);
        if (droneId.isPresent()) {
            droneName = droneId.get();
        }

        return droneName;
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
