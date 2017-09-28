package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.Map;

public class ArchitectureInstance extends Instance {
    public ArchitectureInstance(Map<String, String> properties) {
        super(Type.SERVICE, Group.SERVICES, "architecture", properties);
    }
}
