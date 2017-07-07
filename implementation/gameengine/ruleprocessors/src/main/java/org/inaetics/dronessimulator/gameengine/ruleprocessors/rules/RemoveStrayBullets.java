package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Bullet;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rule to remove bullets that have strayed from the arena
 */
public class RemoveStrayBullets extends Rule {
    @Override
    public void configRule() {
        // Nothing to config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent event) {
        List<GameEngineEvent> events;

        if(event instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) event;
            List<DestroyBulletEvent> destroyedBullets = this.removeStaleBullets(currentStateEvent);

            events = new ArrayList<>(destroyedBullets.size() + 1);
            events.add(event);
            events.addAll(destroyedBullets);
        } else {
            events = Collections.singletonList(event);
        }


        return events;
    }

    /**
     * Adds message to destroy stray bullets
     * @param currentStateEvent - The current state of drones and bullets
     * @return List of removal events of bullets
     */
    private List<DestroyBulletEvent> removeStaleBullets(CurrentStateEvent currentStateEvent) {
        List<DestroyBulletEvent> events = new ArrayList<>();

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

    /**
     * Checks if a bullet is still in the arena or not
     * @param bullet - The bullet to check
     * @return True if the bullet is still in the arena, otherwise false
     */
    private boolean inArena(Bullet bullet) {
        D3Vector position = bullet.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        return x >= 0 && x <= Settings.ARENA_WIDTH
            && y >= 0 && y <= Settings.ARENA_DEPTH
            && z >= 0 && z <= Settings.ARENA_HEIGHT
            ;
    }
}
