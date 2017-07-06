package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveStrayBullets extends Processor {
    // TODO Make arena dimensions via config
    private static final int ARENA_WIDTH = 1024;
    private static final int ARENA_HEIGHT = 100;
    private static final int ARENA_DEPTH = 1024;

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

                if(!inArena(bullet)) {
                    events.add(new DestroyBulletEvent(bullet.getEntityId()));
                }
            }
        }

        return events;
    }

    private boolean inArena(Bullet bullet) {
        D3Vector position = bullet.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        return x >= 0 && x <= ARENA_WIDTH
            && y >= 0 && y <= ARENA_DEPTH
            && z >= 0 && z <= ARENA_HEIGHT
            ;
    }
}
