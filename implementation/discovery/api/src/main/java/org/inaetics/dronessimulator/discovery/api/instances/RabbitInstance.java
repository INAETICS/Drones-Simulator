package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.Map;

public class RabbitInstance extends Instance {
    public RabbitInstance() {
        super(Type.RABBITMQ, Group.BROKER, "default");
    }
}
