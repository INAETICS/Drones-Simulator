package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyBulletEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.DestroyHealthEntityEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;

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
        if(msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;

            for(Integer killedId : killedEntities) {
                currentStateEvent.removeEntity(killedId);
            }
        } else {
            if(msg instanceof DestroyBulletEvent) {
                killedEntities.add(((DestroyBulletEvent) msg).getId());
            } else if(msg instanceof DestroyHealthEntityEvent) {
                killedEntities.add(((DestroyHealthEntityEvent) msg).getDestroyedEntity().getEntityId());
            }
        }

        return Collections.emptyList();
    }
}
