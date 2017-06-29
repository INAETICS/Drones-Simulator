package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveStrayBullets extends Processor {
    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent event) {
        List<GameEngineEvent> events;

        if(event instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) event;

            events = this.removeStaleBullets(currentStateEvent);
        } else {
            events = Collections.emptyList();
        }


        return events;
    }

    private List<GameEngineEvent> removeStaleBullets(CurrentStateEvent currentStateEvent) {
        List<GameEngineEvent> events = new ArrayList<>();

        for(GameEntity entity : currentStateEvent.getCurrentState()) {
            if(entity instanceof Bullet) {
                Bullet bullet = (Bullet) entity;

                // TODO Make range dependent on arena size
                if(bullet.getPosition().length() >= 565) {
                    events.add(new DestroyBulletEvent(bullet.getEntityId()));
                }
            }
        }

        return events;
    }
}
