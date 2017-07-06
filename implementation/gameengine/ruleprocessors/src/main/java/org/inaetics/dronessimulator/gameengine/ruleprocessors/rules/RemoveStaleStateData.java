package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.gameengine.common.gameevent.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoveStaleStateData extends Processor {
    private final Set<Integer> killedEntities;

    public RemoveStaleStateData() {
        this.killedEntities = new HashSet<>();
    }

    @Override
    public void configRule() {
        killedEntities.clear();
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        List<GameEngineEvent> result;
        boolean passMessage = true;

        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;

            for(Integer killedId : killedEntities) {
                currentStateEvent.removeEntity(killedId);
            }
        } else if(msg instanceof CollisionStartEvent) {
            CollisionStartEvent event = (CollisionStartEvent) msg;

            if(isAlreadyDead(event.getE1().getEntityId()) || isAlreadyDead(event.getE2().getEntityId())) {
                passMessage = false;
            }
        } else if(msg instanceof CollisionEndEvent) {
            CollisionEndEvent event = (CollisionEndEvent) msg;

            if(isAlreadyDead(event.getE1().getEntityId()) || isAlreadyDead(event.getE2().getEntityId())) {
                passMessage = false;
            }
        } else if(msg instanceof DamageEvent) {
            DamageEvent event = (DamageEvent) msg;

            if(isAlreadyDead(event.getEntity().getEntityId())) {
                passMessage = false;
            }
        } else {
            if(msg instanceof DestroyBulletEvent) {
                killedEntities.add(((DestroyBulletEvent) msg).getId());
            } else if(msg instanceof DestroyHealthEntityEvent) {
                killedEntities.add(((DestroyHealthEntityEvent) msg).getDestroyedEntity().getEntityId());
            }
        }

        if(passMessage) {
            result = Collections.singletonList(msg);
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    public boolean isAlreadyDead(Integer entityId) {
        return killedEntities.contains(entityId);
    }
}
