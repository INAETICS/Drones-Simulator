package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyHealthEntityEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.common.state.HealthGameEntity;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule to kill entities when their hp <= 0
 */
public class KillEntitiesRule extends Rule {
    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results;

        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            List<DestroyHealthEntityEvent> killedEntities = killDeadEntities(currentStateEvent.getCurrentState());

            results = new ArrayList<>(killedEntities.size() + 1);
            results.add(msg);
            results.addAll(killedEntities);
        } else {
            results = Collections.singletonList(msg);
        }

        return results;
    }

    /**
     * Process the list of entities to check for entities to kill
     * @param gameEntities The entities to check
     * @return The entities to kill
     */
    private List<DestroyHealthEntityEvent> killDeadEntities(List<GameEntity> gameEntities) {
        List<DestroyHealthEntityEvent> results = new ArrayList<>(gameEntities.size());

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
