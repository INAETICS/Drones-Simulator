package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyHealthEntityEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillOutOfBounds extends Rule {
    @Override
    public void configRule() {
        // nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results;

        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            List<DestroyHealthEntityEvent> killedEntities = killCrashedEntities(currentStateEvent.getCurrentState());

            results = new ArrayList<>(killedEntities.size() + 1);
            results.add(msg);
            results.addAll(killedEntities);
        } else {
            results = Collections.singletonList(msg);
        }

        return results;
    }

    private List<DestroyHealthEntityEvent> killCrashedEntities(List<GameEntity> gameEntities) {
        List<DestroyHealthEntityEvent> results = new ArrayList<>(gameEntities.size());

        for(GameEntity gameEntity : gameEntities) {
            if(gameEntity instanceof HealthGameEntity) {
                HealthGameEntity healthGameEntity = (HealthGameEntity) gameEntity;

                if(healthGameEntity.getPosition().getY() < 0 ||
                        healthGameEntity.getPosition().getY() > Settings.ARENA_DEPTH ||
                        healthGameEntity.getPosition().getZ() < 0 ||
                        healthGameEntity.getPosition().getZ() > Settings.ARENA_HEIGHT ||
                        healthGameEntity.getPosition().getX() < 0 ||
                        healthGameEntity.getPosition().getX() > Settings.ARENA_WIDTH) {
                    DestroyHealthEntityEvent destroyEvent = new DestroyHealthEntityEvent(healthGameEntity);

                    results.add(destroyEvent);
                }
            }
        }

        return results;
    }
}
