package org.inaetics.dronessimulator.physicsengine;

import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.io.IOException;
import java.util.List;

public class PhysicsEngineBundle {
    private volatile Publisher m_publisher;

    private final PollThread pollThread;
    private final PhysicsEngine physicsEngine;

    public PhysicsEngineBundle() {
        this.physicsEngine = new PhysicsEngine();
        this.pollThread = new PollThread();

        physicsEngine.start();
        pollThread.start();
    }

    public void stop() throws Exception {
        pollThread.quit();
        physicsEngine.quit();

        pollThread.join();
        physicsEngine.join();
    }

    private class PollThread extends Thread {
        private long POLLRATE = 10; // in ms
        private volatile boolean quit = false;

        public void run() {
            this.quit = false;
            Thread t = Thread.currentThread();

            while(!t.isInterrupted()) {
                List<Entity> entities = physicsEngine.getCurrentState();

                broadcastState(entities);

                try {
                    Thread.sleep(POLLRATE);
                } catch (InterruptedException e) {
                    t.interrupt();
                }
            }
            this.quit = true;
        }

        private void broadcastState(List<Entity> entities) {
            for(Entity entity : entities) {
                StateMessage msg = new StateMessage();
                msg.setAcceleration(entity.getAcceleration());
                msg.setVelocity(entity.getVelocity());
                msg.setPosition(entity.getPosition());

                try {
                    m_publisher.send(MessageTopic.STATEUPDATES, msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void quit() {
            this.interrupt();
        }

        public boolean hasQuit() {
            return this.quit;
        }
    }

  public static void main(String[] args) {
        try {
            PhysicsEngineBundle bundle = new PhysicsEngineBundle();
            Thread.sleep(1000);

            bundle.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }


  }
}
