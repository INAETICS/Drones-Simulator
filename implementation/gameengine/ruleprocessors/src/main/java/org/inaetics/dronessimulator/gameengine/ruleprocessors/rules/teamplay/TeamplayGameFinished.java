package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.teamplay;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.AbstractGameFinishedRule;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamplayGameFinished extends AbstractGameFinishedRule {
    private Map<String, Long> dronesPerTeam;

    public TeamplayGameFinished(IdentifierMapper idMapper) {
        super(idMapper);
    }

    @Override
    protected boolean gameIsFinished(List<GameEntity> currentState) {
        dronesPerTeam = currentState.stream().filter(gameEntity -> EntityType.DRONE.equals(gameEntity.getType())).map(gameEntity -> ((Drone) gameEntity))
                .filter(drone -> drone.getHp() > 0).collect(Collectors.groupingBy(
                        Drone::getTeam, Collectors.counting()
                ));
        return dronesPerTeam.size() == 1;
    }

    @Override
    protected String getWinner() {
        return dronesPerTeam.keySet().iterator().next();
    }
}
