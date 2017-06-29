package org.inaetics.dronessimulator.physicsengine;


import lombok.Getter;
import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages the entities in the simulated world. Processes any changes to entities.
 * This class is partially threadsafe. All requested changes are buffered and processed when any or all of the process
 * 'stages' are called.
 */
@Getter
public class EntityManager {
    /** Updates to be processed by entity id. */
    private final ConcurrentMap<Integer, ConcurrentLinkedQueue<EntityUpdate>> updateMap;

    /** Entities to add. */
    private final ConcurrentLinkedQueue<Entity> creationList;

    /** Entities to remove. */
    private final ConcurrentLinkedQueue<Integer> removalList;

    /** Currently present entities, by id. */
    private final Map<Integer, Entity> entities;

    /** Currently present collisions, by id. */
    private final HashMap<Integer, Set<Integer>> currentCollisions;

    /**
     * Instantiates a new entity manager.
     * @param currentCollisions The currently present collisions.
     */
    public EntityManager(HashMap<Integer, Set<Integer>> currentCollisions) {
        this.creationList = new ConcurrentLinkedQueue<>();
        this.updateMap = new ConcurrentHashMap<>(100);
        this.removalList = new ConcurrentLinkedQueue<>();

        this.entities = new HashMap<>(100);

        this.currentCollisions = currentCollisions;
    }

    /**
     * Adds the new entities to this manager. Overwrites existing entities with the same ids.
     * @threadsafe
     * @param creations The new entities to add.
     */
    public void addInserts(Collection<Entity> creations) {
        this.creationList.addAll(creations);
    }

    /**
     * Adds the new entity to this manager. Overwrites an existing entity with the same id.
     * @threadsafe
     * @param creation The new entity to add
     */
    public void addInsert(Entity creation) {
        this.creationList.add(creation);
    }

    /**
     * Updates the entity with the given id by applying the given updates.
     * @threadsafe
     * @param entityId The id of the entity to update.
     * @param updates The updates to apply to the entity.
     */
    public void addUpdates(Integer entityId, Collection<EntityUpdate> updates) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).addAll(updates);
    }

    /**
     * Updates the entity with the given id by applying the update.
     * @threadsafe
     * @param entityId The id of the entity to update.
     * @param update The update to apply to the entity.
     */
    public void addUpdate(Integer entityId, EntityUpdate update) {
        this.updateMap.putIfAbsent(entityId, new ConcurrentLinkedQueue<>());
        this.updateMap.get(entityId).add(update);
    }

    /**
     * Removes the entities from this manager.
     * @threadsafe
     * @param removals The ids of the entities to remove.
     */
    public void addRemovals(Collection<Integer> removals) {
        this.removalList.addAll(removals);
    }

    /**
     * Removes the entity from this manager.
     * @threadsafe
     * @param removal The id of the entity to remove.
     */
    public void addRemoval(Integer removal) {
        this.removalList.add(removal);
    }

    public void addRemoveAll() {
        this.addRemovals(this.entities.keySet());
    }

    /**
     * Process all incoming entity insertion requests.
     */
    private void processInsertNew() {
        while(!creationList.isEmpty()) {
            Entity entity = creationList.poll();
            updateMap.putIfAbsent(entity.getId(), new ConcurrentLinkedQueue<>());
            entities.put(entity.getId(), entity);

            this.currentCollisions.put(entity.getId(), new HashSet<>());
        }
    }

    /**
     * Process all incoming entity update requests.
     */
    private void processUpdate() {
        // TODO: Remove memory leak for updates that belong to non-existing entities
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

            this.currentCollisions.remove(removeEntityId);
        }
    }

    /**
     * Deep copy the current state.
     * @return A deep copy of all entities
     */
    public List<Entity> copyState() {
        List<Entity> result = new ArrayList<>(this.entities.size());

        for(Map.Entry<Integer, Entity> e : this.entities.entrySet()) {
            result.add(Entity.deepcopy(e.getValue()));
        }

        return result;
    }

    /**
     * Process all requests for entity changes in this manager.
     */
    public void processChanges() {
        this.processInsertNew();
        this.processUpdate();
        this.processRemoval();
    }

    public void clear() {
        this.currentCollisions.clear();
        this.entities.clear();

        this.updateMap.clear();
        this.creationList.clear();
        this.removalList.clear();
    }
}
