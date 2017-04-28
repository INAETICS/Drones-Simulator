package org.inaetics.dronessimulator.physicsenginewrapper.state;

import lombok.Getter;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.physicsengine.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

@Getter
public class PhysicsEngineStateManager {
    private final HashMap<Integer, PhysicsEngineEntity> state;


    public PhysicsEngineStateManager() {
        this.state = new HashMap<>();
    }


    public void addEntityState(PhysicsEngineEntity entity) {
        int id = entity.getEntityId();

        this.state.put(id, entity);
    }

    public void removeState(Integer entityId) {
        this.state.remove(entityId);
    }

    public EntityType getTypeFor(Integer id) {
        return this.state.get(id).getType();
    }

    public PhysicsEngineEntity getById(Integer id) {
        return this.state.get(id);
    }

    public List<PhysicsEngineEntity> getWithType(EntityType type) {
        return this.state.entrySet()
                         .stream()
                         .map(Map.Entry::getValue)
                         .filter((e) -> e.getType().equals(type))
                         .collect(Collectors.toList());
    }

    public void updateState(List<Entity> entities) {
        for(Entity entity : entities) {
            int id = entity.getId();
            PhysicsEngineEntity stateEntity = this.state.get(id);

            if(stateEntity != null) {
                stateEntity.updateFromEngine(entity);
            } else {
                Logger.getLogger(PhysicsEngineStateManager.class).fatal("Tried to update complete state. Found entity in engine which is not in state. Engine id: " + id);
            }

        }
    }
}
