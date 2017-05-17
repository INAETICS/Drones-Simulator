package org.inaetics.dronessimulator.gameengine.common.gameevent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.inaetics.dronessimulator.common.protocol.ProtocolMessage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DestroyBulletEvent extends GameEngineEvent {
    private final int id;

    @Override
    public List<ProtocolMessage> getProtocolMessage() {
        // Do not broadcast when a bullet is killed

        return Collections.emptyList();
    }
}
