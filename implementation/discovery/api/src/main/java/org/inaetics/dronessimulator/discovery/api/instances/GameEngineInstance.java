package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

public class GameEngineInstance extends Instance {
    public GameEngineInstance() {
        super(Type.SERVICE, Group.SERVICES, "gameengine");
    }
}
