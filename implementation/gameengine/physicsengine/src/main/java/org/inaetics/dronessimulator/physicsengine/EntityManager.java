package org.inaetics.dronessimulator.physicsengine;


import lombok.Getter;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Getter
/**
 * Manages the entities in the simulated world. Processes any changes to which entity exist and
 * their characteristics. Is partially threadsafe. All requested changes are buffered and processed
 * when any or all of the process'stage' are called.
 */
public class EntityManager {
    /**
     * Any updates to process later to the entity identified by the key as id
     */
    private final ConcurrentMap<Integer, ConcurrentLinkedQueue<EntityUpdate>> updateMap;
    /**
     * Which entities to add later as changes are processed
     */
    private final ConcurrentLinkedQueue<Entity> creationList;
    /**
     * Which entities to remove later as changes are processed
     */
    private final ConcurrentLinkedQueue<Integer> removalList;

    /**
     * Which entities currently exist in this manager
     */
    private final Map<Integer, Entity> entities;

    public EntityManager() {
        this.creationList = new ConcurrentLinkedQueue<>();
        this.updateMap = new ConcurrentHashMap<>(100);
        this.removalList = new ConcurrentLinkedQueue<>();

        this.entities = new HashMap<>(100);
    }

    /**
     * Add the new entities to this manager. Overwrites existing entity
     * @threadsafe
     * @param creations Which new entities to add
     */
    public void addInserts(Collection<Entity> creations) {
        this.creationList.addAll(creations);
    }

    /**
     * Add the new entity to this manager. Overwrites existing entity
     * @threadsafe
     * @param creation Which new entity to add
     */
    public void addInsert(Entity creation) {
        this.creationList.add(creation);
    }

    /**
     * Updates the entity with id entityId by applying all updates.
     * @threadsafe
     * @param entityId Which entity to update
     * @param updates Which updates to apply to the entity
     */
    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).addAll(updates);
    }

    /**
     * Updates the entity with id entityId by applying the update.
     * @threadsafe
     * @param entityId Which entity to update
     * @param update Which update to apply to the entity
     */
    public void addUpdate(Integer entityId, EntityUpdate update) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).add(update);
    }

    /**
     * Which entities shall be removed
     * @threadsafe
     * @param removals Which entities to remove by id
     */
    public void addRemovals(Collection<Integer> removals) {
        this.removalList.addAll(removals);
    }

    /**
     * Which entities shall be removed
     * @threadsafe
     * @param removal Which entities to remove by id
     */
    public void addRemoval(Integer removal) {
        this.removalList.add(removal);
    }

    /**
     * Process all incoming entity insertion requests.
     */
    private void processInsertNew() {
        while(!creationList.isEmpty()) {
            Entity entity = creationList.poll();
            updateMap.putIfAbsent(entity.getId(), new ConcurrentLinkedQueue<>());
            entities.put(entity.getId(), entity);
        }
    }

    /**
     * Process all incoming entity update requests.
     */
    private void processUpdate() {
        for(Map.Entry<Integer, Entity> e : entities.entrySet()) {
            Entity entity = e.getValue();
            ConcurrentLinkedQueue<EntityUpdate> updates = updateMap.get(entity.getId());

            while(!updates.isEmpty()) {
                updates.poll().update(entity);
            }
        }
    }

    /**
     * Process all incoming entity removal requests.
     */
    private void processRemoval() {
        while(!removalList.isEmpty()) {
            Integer removeEntityId = removalList.poll();
            updateMap.remove(removeEntityId);
            entities.remove(removeEntityId);
        }
    }

    /**
     * Deepcopy the current state.
     * @return A deepcopy of all entities
     */
    public List<Entity> copyState() {
        List<Entity> result = new ArrayList<>(this.entities.size());

        for(Map.Entry<Integer, Entity> e : this.entities.entrySet()) {
            result.add(Entity.deepcopy(e.getValue()));
        }

        return result;
    }

    /**
     * Process all requests changes to this manager
     */
    public void processChanges() {
        this.processInsertNew();
        this.processUpdate();
        this.processRemoval();
    }
}
