package org.inaetics.dronesimulator.physicsengine;

import org.inaetics.dronesimulator.common.D3Vector;

public class PhysicsEngine {
    public static final D3Vector GRAVITY = new D3Vector(0, 0, -9.81);

    private Entity[] entities = { new Entity(1, new Size(1, 1, 1), new D3Vector(0, 0, 0), new D3Vector(0, 0, 0), new D3Vector(1, 0, 0))
                                , new Entity(2, new Size(1, 1, 1), new D3Vector(10, 0, 0), new D3Vector(0, 0, 0), new D3Vector(-1, 0, 0))
                                };
    private long last_step_at;
    private boolean not_quit = true;

    public PhysicsEngine() {
        this.last_step_at = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        new PhysicsEngine().runServer();
    }

    private D3Vector environmentForces(Entity entity) {
        return GRAVITY;
    }

    private D3Vector collionForce(Entity subject, Entity collision) {
        return null;
    }

    private void stageMove(double timestep_s) {
        for(Entity entity : entities) {
            D3Vector nextAcceleration = entity.getAcceleration();
            D3Vector nextVelocity = entity.nextVelocity(environmentForces(entity).add(nextAcceleration), timestep_s);
            D3Vector nextPosition = entity.nextPosition(nextVelocity, timestep_s);


            Entity newEntity = new Entity(entity.getId(), entity.getSize(), nextPosition, nextVelocity, nextAcceleration);
            boolean collides = false;

            for(Entity otherEntity : entities) {
                if(!entity.equals(otherEntity) && newEntity.collides(otherEntity)) {
                    collides = true;
                }
            }

            if(!collides) {
                entity.setAcceleration(nextAcceleration);
                entity.setVelocity(nextVelocity);
                entity.setPosition(nextPosition);
            }
        }
    }

    public void runServer() {
        long last_log_at = 0;

        while(not_quit) {
            long current_ms = System.currentTimeMillis();
            long timestep_ms = current_ms - last_step_at;
            double timestep_s = ((float) timestep_ms) / 1000;
            this.last_step_at = current_ms;



            this.stageMove(timestep_s);

            if((current_ms - last_log_at) > 500) {
                System.out.println();
                System.out.println(entities[0]);
                System.out.println(entities[1]);
                last_log_at = current_ms;
            }

            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {

            }
        }
    }

    public void quit() {
        this.not_quit = false;
    }
}
