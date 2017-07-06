package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyHealthEntityEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.Processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillEntitiesRule extends Processor {
    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results;

        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;

            results = killDeadEntities(currentStateEvent.getCurrentState());
            results.add(0, msg);
        } else {
            results = Collections.singletonList(msg);
        }

        return results;
    }

    private List<GameEngineEvent> killDeadEntities(List<GameEntity> gameEntities) {
        List<GameEngineEvent> results = new ArrayList<>(gameEntities.size());

        for(GameEntity gameEntity : gameEntities) {
            if(gameEntity instanceof HealthGameEntity) {
                HealthGameEntity healthGameEntity = (HealthGameEntity) gameEntity;

                if(healthGameEntity.getHP() <= 0) {
                    DestroyHealthEntityEvent destroyEvent = new DestroyHealthEntityEvent(healthGameEntity);

                    results.add(destroyEvent);
                }
            }
        }

        return results;
    }
}
