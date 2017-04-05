package org.inaetics.isep;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.List;

public class PhysicsEngineBundle implements BundleActivator {
    private final PollThread pollThread;
    private final PhysicsEngine physicsEngine;

    public PhysicsEngineBundle() {
        this.physicsEngine = new PhysicsEngine();
        this.pollThread = new PollThread();
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        physicsEngine.start();
        pollThread.start();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        pollThread.quit();
        pollThread.join();

        physicsEngine.quit();
        physicsEngine.join();
    }

    private class PollThread extends Thread {
        private long POLLRATE = 100; // in ms
        private volatile boolean quit = false;

        public void run() {
            while(!quit) {

                List<Entity> entities = physicsEngine.getCurrentState();

                broadcastState(entities);

                try {
                    Thread.sleep(POLLRATE);
                } catch (InterruptedException e) {
                }
            }
        }

        private void broadcastState(List<Entity> entities) {
            //TODO how to broadcast state
            System.out.println(entities);
        }

        public void quit() {
            this.quit = quit;
        }
    }

  public static void main(String[] args) {
        PhysicsEngineBundle bundle = new PhysicsEngineBundle();

        try {
            bundle.start(null);

            Thread.sleep(1000);

            bundle.stop(null);

        } catch (Exception e) {
            e.printStackTrace();
        }


  }


}
