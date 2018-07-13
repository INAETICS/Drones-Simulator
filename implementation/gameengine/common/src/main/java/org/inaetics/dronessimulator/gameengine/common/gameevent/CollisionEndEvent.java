package org.inaetics.dronessimulator.gameengine.common.gameevent;

import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * A unified physics engine message which contains the end of a collision between two game entities.
 */
public class CollisionEndEvent extends GameEngineEvent {
    public CollisionEndEvent(GameEntity e1, GameEntity e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /** First entity in the collision. */
    private final GameEntity e1;

    /** Second entity in the collision. */
    private final GameEntity e2;

    @Override
    public List<ProtocolMessage> getProtocolMessage(IdentifierMapper id_mapper) {
        // Do not need to broadcast any collision end messages (yet)
        return new ArrayList<>();
    }

    public GameEntity getE1() {
        return e1;
    }

    public GameEntity getE2() {
        return e2;
    }
}
