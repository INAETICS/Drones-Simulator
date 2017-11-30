package org.inaetics.dronessimulator.physicsengine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.Size;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import static org.inaetics.dronessimulator.common.Settings.MAX_DRONE_ACCELERATION;
import static org.inaetics.dronessimulator.common.Settings.MAX_DRONE_VELOCITY;

/**
 * An entity represented in the simulated world.
 * x == width, y == depth, z == height
 */

@Getter
@Setter
@ToString
public class Entity extends GameEntity implements Cloneable {
    /**
     * Creates an entity.
     *
     * @param id   The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     */
    public Entity(int id, Size size) {
        super(id, size, new D3Vector(), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id   The id of the new entity.
     * @param size The size of the non-rotating hitbox of the entity.
     * @param x    The position of the new entity on the x-axis in the world.
     * @param y    The position of the new entity on the y-axis in the world.
     * @param z    The position of the new entity on the z-axis in the world.
     */
    public Entity(int id, Size size, double x, double y, double z) {
        super(id, size, new D3Vector(x, y, z), new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id       The id of the new entity.
     * @param size     The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position) {
        this(id, size, position, new D3Vector(), new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id       The id of the new entity.
     * @param size     The size of the non-rotating hitbox of the entity.
     * @param position The position of the new entity in the world.
     * @param velocity The velocity of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity) {
        super(id, size, position, velocity, new D3Vector(), new D3PolarCoordinate());
    }

    /**
     * Creates an entity.
     *
     * @param id        The id of the new entity.
     * @param size      The size of the non-rotating hitbox of the entity.
     * @param position  The position of the new entity in the world.
     * @param velocity  The velocity of the new entity in the world.
     * @param direction The direction of the new entity in the world.
     */
    public Entity(int id, Size size, D3Vector position, D3Vector velocity, D3PolarCoordinate direction) {
        super(id, size, position, velocity, new D3Vector(), direction);
    }

    public Entity(int entityId, Size size, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction) {
        super(entityId, size, position, velocity, acceleration, direction);
    }

    /**
     * Returns a deep copy of the given entity.
     *
     * @param entity The entity to copy.
     * @return A copy of the entity.
     */
    public static Entity deepcopy(Entity entity) {
        return new Entity(entity.getEntityId(), entity.getSize(), entity.getPosition(), entity.getVelocity(), entity
                .getAcceleration(), entity.getDirection());
    }

    /**
     * Moves the entity in the world using the set velocity and acceleration for the time step.
     *
     * @param time_in_seconds The time step in seconds.
     */
    public void move(double time_in_seconds) {
        setVelocity(this.nextVelocity(getAcceleration(), time_in_seconds));
        setPosition(this.nextPosition(getVelocity(), time_in_seconds));
    }

    /**
     * Calculates the velocity for this entity after the given period of the given acceleration.
     *
     * @param nextAcceleration The change in velocity during time step.
     * @param time_in_seconds  The time step is in seconds.
     * @return The next velocity for this entity.
     */
    public D3Vector nextVelocity(D3Vector nextAcceleration, double time_in_seconds) {
        return nextAcceleration.scale(time_in_seconds).add(getVelocity());
    }

    /**
     * Calculates the position of this entity after the given period of the given velocity.
     *
     * @param nextVelocity    The change in position during this time step.
     * @param time_in_seconds How long the time step is in seconds.
     * @return The next position for this entity.
     */
    public D3Vector nextPosition(D3Vector nextVelocity, double time_in_seconds) {
        return nextVelocity.scale(time_in_seconds).add(getPosition());
    }

    /**
     * Test whether this entity and the given entity are colliding at their current position.
     *
     * @param other The other entity.
     * @return If the entities are colliding.
     */
    public boolean collides(Entity other) {
        boolean xOverlap = (this.getMinX() <= other.getMaxX() && this.getMaxX() >= other.getMinX());
        boolean yOverlap = (this.getMinY() <= other.getMaxY() && this.getMaxY() >= other.getMinY());
        boolean zOverlap = (this.getMinZ() <= other.getMaxZ() && this.getMaxZ() >= other.getMinZ());

        return xOverlap && yOverlap && zOverlap;
    }

    /**
     * Returns the minimal x coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal x coordinate.
     */
    public double getMinX() {
        return getPosition().getX() - 0.5 * getSize().getWidth();
    }

    /**
     * Returns the minimal y coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal y coordinate.
     */
    public double getMinY() {
        return getPosition().getY() - 0.5 * getSize().getDepth();
    }

    /**
     * Returns the minimal z coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The minimal z coordinate.
     */
    public double getMinZ() {
        return getPosition().getZ() - 0.5 * getSize().getHeight();
    }

    /**
     * Returns the maximal x coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal x coordinate.
     */
    public double getMaxX() {
        return getPosition().getX() + 0.5 * getSize().getWidth();
    }

    /**
     * Returns the maximal y coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal y coordinate.
     */
    public double getMaxY() {
        return getPosition().getY() + 0.5 * getSize().getDepth();
    }

    /**
     * Returns the maximal z coordinate of the space this entity occupies determined by its position and size.
     *
     * @return The maximal z coordinate.
     */
    public double getMaxZ() {
        return getPosition().getZ() + 0.5 * getSize().getHeight();
    }

    /**
     * Tests whether this entity and the given entity are equal.
     *
     * @param other The other entity.
     * @return Whether the entities are equal.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Entity && this.getEntityId() == ((Entity) other).getEntityId();
    }

    @Override
    public int hashCode() {
        return this.getEntityId();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public GameEntity deepCopy() {
        return deepcopy(this);
    }


    @Log4j
    public static class DroneEntity extends Entity {

        @Getter
        @Setter
        private D3Vector targetPosition;

        public DroneEntity(int id, Size size, D3Vector position, D3Vector velocity, D3Vector acceleration, D3PolarCoordinate direction, D3Vector targetPosition) {
            super(id, size, position, velocity, acceleration, direction);
            this.targetPosition = targetPosition;
        }

        public D3Vector getAcceleration() {
            D3Vector nextAcceleration;
            if (getTargetPosition() != null && !targetPosition.equals(new D3Vector()) && !targetPosition.equals(getPosition())) {
                nextAcceleration = calculateAccelrationToGetToPosition(getTargetPosition(), getPosition(), getVelocity());
            } else {
                nextAcceleration = super.getAcceleration();
                targetPosition = null;
            }
            return nextAcceleration;
        }

        private D3Vector calculateAccelrationToGetToPosition(D3Vector targetLocation, D3Vector currentLocation, D3Vector
                currentVelocity) {
            log.info("Moving to " + targetLocation.toString() + " from " + currentLocation.toString());
            if (currentLocation.distance_between(targetLocation) < 1) {
                if (currentVelocity.length() != 0) {
                    D3Vector move = limit_acceleration(currentVelocity.scale(-1));
                    log.info("WE ARE CLOSE!" + move.toString());
                    return move;
                }
            } else {
                D3Vector targetAcceleration;
                double distance = currentLocation.distance_between(targetLocation);
                double decelDistance = (currentVelocity.length() * currentVelocity.length()) / (2 * Settings
                        .MAX_DRONE_ACCELERATION);
                if (distance > decelDistance) //we are still far, continue accelerating (if possible)
                {
                    targetAcceleration = maximize_acceleration(targetLocation.sub(currentLocation));
                } else    //we are about to reach the target, let's start decelerating.
                {
                    targetAcceleration = currentVelocity.normalize().scale(-(currentVelocity.length() * currentVelocity
                            .length()) / (2 * distance));
                }
//            D3Vector move = location.sub(position.add(currentVelocity));
                log.info("WE ARE NOT CLOSE!" + targetAcceleration.toString());
                return targetAcceleration;
            }
            return new D3Vector();
        }

        /**
         * Limit the acceleration
         *
         * @param input The acceleration to limit
         * @return The limited acceleration
         */
        public D3Vector limit_acceleration(D3Vector input) {
            D3Vector output = input;
            // Prevent that the acceleration exceeds te maximum acceleration
            if (input.length() > MAX_DRONE_ACCELERATION) {
                double correctionFactor = MAX_DRONE_ACCELERATION / input.length();
                output = input.scale(correctionFactor);
            }
            return output;
        }

        /**
         * Maximizes the acceleration in the same direction
         *
         * @param input The vector to scale to the maximal acceleration value
         * @return The vector in the same direction as input but length == max acceleration value
         */
        public D3Vector maximize_acceleration(D3Vector input) {
            D3Vector output = input;
            if (input.length() < MAX_DRONE_ACCELERATION && input.length() != 0) {
                double correctionFactor = MAX_DRONE_ACCELERATION / input.length();
                output = input.scale(correctionFactor);
            }
            return output;
        }


        /**
         * Limits the velocity when the maximum velocity is archieved.
         *
         * @param input acceleration as a D3Vector
         * @return optimized acceleration as a D3Vector
         */
        private D3Vector limit_velocity(D3Vector input) {
            D3Vector output = input;
            // Check velocity
            if (getVelocity().length() >= MAX_DRONE_VELOCITY && getVelocity().add(input).length() >= getVelocity().length()) {
                output = new D3Vector();
            }
            return output;
        }

        /**
         * Stagnate the acceleration when the velocity is at 90% of the maximum velocity.
         *
         * @param input acceleration as a D3Vector
         * @return optimized acceleration as a D3Vector
         */
        public D3Vector stagnate_acceleration(D3Vector input) {
            D3Vector output = input;
            // Change acceleration if velocity is close to the maximum velocity
            if (getVelocity().length() >= (MAX_DRONE_VELOCITY * 0.9)) {
                double maxAcceleration = MAX_DRONE_VELOCITY - getVelocity().length();
                if (Math.abs(output.length()) > Math.abs(maxAcceleration)) {
                    output = output.scale(maxAcceleration / output.length() == 0 ? 1 : output.length());
                }
            }
            return output;
        }

        /**
         * Set the new desired acceleration
         *
         * @param input_acceleration The new acceleration for the drone using this component
         */
        public void changeAcceleration(D3Vector input_acceleration) {
            D3Vector acceleration = input_acceleration;

            acceleration = limit_acceleration(acceleration);
            acceleration = limit_velocity(acceleration);
            acceleration = stagnate_acceleration(acceleration);

            if (Double.isNaN(acceleration.getX()) || Double.isNaN(acceleration.getY()) || Double.isNaN(acceleration.getZ())) {
                throw new IllegalArgumentException("Acceleration is not a number. Input acceleration: " +
                        "" + input_acceleration.toString() + ", Output acceleration: " + acceleration.toString());
            }

            setAcceleration(acceleration);
        }
    }
}
