package org.inaetics.dronessimulator.gameengine.common.gameevent;

import org.inaetics.dronessimulator.common.protocol.CollisionMessage;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A unified physics engine message which contains the start of a collision between two game entities.
 */
public class CollisionStartEvent extends GameEngineEvent {
    public CollisionStartEvent(GameEntity e1, GameEntity e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /** First entity in the collision. */
    private final GameEntity e1;

    /** Second entity in the collision. */
    private final GameEntity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        CollisionMessage msg = new CollisionMessage();
        Optional<String> maybeE1 = id_mapper.fromGameEngineToProtocolId(this.e1.getEntityId());
        Optional<String> maybeE2 =  id_mapper.fromGameEngineToProtocolId(this.e2.getEntityId());

        if(maybeE1.isPresent() && maybeE2.isPresent()) {
            msg.setE1Identifier(maybeE1.get());
            msg.setE1Type(this.e1.getType());

            msg.setE2Identifier(maybeE2.get());
            msg.setE2Type(this.e2.getType());

            return Collections.singletonList(msg);
        } else {
            return Collections.emptyList();
        }

    }

    public GameEntity getE1() {
        return e1;
    }

    public GameEntity getE2() {
        return e2;
    }
}
