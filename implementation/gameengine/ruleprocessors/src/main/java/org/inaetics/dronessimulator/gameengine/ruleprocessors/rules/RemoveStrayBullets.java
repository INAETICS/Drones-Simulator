package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.ArrayList;
import java.util.List;

public class RemoveStrayBullets extends Processor {
    @Override
    public void configRule() {}

    @Override
    public List<GameEngineEvent> process(GameEngineEvent event) {
        List<GameEngineEvent> events = new ArrayList<>();

        if(event instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) event;

            for(GameEntity entity : currentStateEvent.getCurrentState()) {

                if(entity instanceof Bullet) {
                    Bullet bullet = (Bullet) entity;

                    // TODO Make range dependent on arena size
                    if(bullet.getPosition().length() >= 565) {
                        events.add(new DestroyBulletEvent(bullet.getEntityId()));
                    }

                }
            }
        }


        return events;
    }
}
