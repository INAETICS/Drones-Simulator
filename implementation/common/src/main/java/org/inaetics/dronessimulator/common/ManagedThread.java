package org.inaetics.dronessimulator.common;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A manageable thread which can start, stop, pause and resume
 */
public abstract class ManagedThread extends Thread {
     /**
     * Thread management if the thread has started
     */
    private final AtomicBoolean started = new AtomicBoolean(false);


    private final AtomicBoolean quit = new AtomicBoolean(false);
    private final AtomicBoolean pauseToken = new AtomicBoolean(false);

    @Override
    public void run() {
        while(!this.isInterrupted()){
            try {
                //Wait for start
                synchronized (started) {
                    while(!started.get()) {
                        started.wait();
                    }
                }

                this.onStart();
                quit.set(false);
                pauseToken.set(false);

                // Work until quit
                while(!quit.get()) {
                    this.work();

                    synchronized (pauseToken) {
                        if(pauseToken.get()) {
                            onPause();
                            while(pauseToken.get()) {
                                pauseToken.wait();
                            }
                            onResume();
                        }
                    }
                }

                this.onStop();
                started.set(false);
            } catch(InterruptedException e) {
                this.interrupt();
            }
        }
    }

    public void startThread() {
        synchronized (started) {
            started.set(true);
            started.notifyAll();
        }
    }

    public void resumeThread() {
        synchronized (pauseToken) {
            pauseToken.set(false);
            pauseToken.notifyAll();
        }
    }

    public void pauseThread() {
        synchronized (pauseToken) {
            pauseToken.set(true);
        }
    }

    public void stopThread() {
        synchronized (quit) {
            quit.set(true);
            this.resumeThread();
        }
    }


    protected void onStart() {}
    protected void onPause() {}
    protected void onResume() {}
    protected void onStop() {}


    protected abstract void work() throws InterruptedException;
}
