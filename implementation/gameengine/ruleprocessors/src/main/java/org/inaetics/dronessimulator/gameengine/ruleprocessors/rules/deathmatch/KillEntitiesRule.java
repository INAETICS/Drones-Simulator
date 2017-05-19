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
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> results;

        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            results  = new ArrayList<>(currentStateEvent.getCurrentState().size());


            for(GameEntity gameEntity : currentStateEvent.getCurrentState()) {
                if(gameEntity instanceof HealthGameEntity) {
                    HealthGameEntity healthGameEntity = (HealthGameEntity) gameEntity;

                    if(healthGameEntity.getHP() <= 0) {
                        DestroyHealthEntityEvent destroyEvent = new DestroyHealthEntityEvent(healthGameEntity);

                        results.add(destroyEvent);
                    }
                }
            }

        } else {
            results = Collections.emptyList();
        }

        return results;
    }
}
