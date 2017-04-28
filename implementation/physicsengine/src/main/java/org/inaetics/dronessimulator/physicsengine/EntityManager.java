package org.inaetics.dronessimulator.physicsengine;


import lombok.Getter;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
class EntityManager {
    private final Map<Integer, ConcurrentLinkedQueue<EntityUpdate>> updateMap;
    private final ConcurrentLinkedQueue<EntityCreation> creationList;
    private final ConcurrentLinkedQueue<Integer> removalList;

    private final Map<Integer, Entity> entities;

    public EntityManager() {
        this.creationList = new ConcurrentLinkedQueue<>();
        this.updateMap = new HashMap<>(100);
        this.removalList = new ConcurrentLinkedQueue<>();

        this.entities = new HashMap<>(100);
    }

    public void addInserts(Collection<EntityCreation> creations) {
        this.creationList.addAll(creations);
    }

    public void addInsert(EntityCreation creation) {
        this.creationList.add(creation);
    }

    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).addAll(updates);
    }

    public void addUpdate(Integer entityId, EntityUpdate update) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).add(update);
    }

    public void addRemovals(Collection<Integer> removals) {
        this.removalList.addAll(removals);
    }

    public void addRemoval(Integer removal) {
        this.removalList.add(removal);
    }

    private void processInsertNew() {
        while(!creationList.isEmpty()) {
            Entity entity = creationList.poll().getNewEntity();
            updateMap.putIfAbsent(entity.getId(), new ConcurrentLinkedQueue<>());
            entities.put(entity.getId(), entity);
        }
    }

    private void processUpdate() {
        for(Map.Entry<Integer, Entity> e : entities.entrySet()) {
            Entity entity = e.getValue();
            ConcurrentLinkedQueue<EntityUpdate> updates = updateMap.get(entity.getId());

            while(!updates.isEmpty()) {
                updates.poll().update(entity);
            }
        }
    }

    private void processRemoval() {
        while(!removalList.isEmpty()) {
            Integer removeEntityId = removalList.poll();
            updateMap.remove(removeEntityId);
            entities.remove(removeEntityId);
        }
    }

    public List<Entity> copyState() {
        List<Entity> result = new ArrayList<>(this.entities.size());

        for(Map.Entry<Integer, Entity> e : this.entities.entrySet()) {
            result.add(Entity.copy(e.getValue()));
        }

        return result;
    }

    public void processChanges() {
        this.processInsertNew();
        this.processUpdate();
        this.processRemoval();
    }
}
