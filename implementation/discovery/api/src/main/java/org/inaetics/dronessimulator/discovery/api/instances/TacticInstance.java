package org.inaetics.dronessimulator.discovery.api.instances;

import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.Map;

public class TacticInstance extends Instance{
    public TacticInstance(String id) {
        super(Type.SERVICE, Group.TACTIC, id);
    }

    public TacticInstance(String id, Map<String, String> properties) {
        super(Type.SERVICE, Group.TACTIC, id, properties);
    }
}
