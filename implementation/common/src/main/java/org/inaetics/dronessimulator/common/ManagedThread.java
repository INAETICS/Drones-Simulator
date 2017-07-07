package org.inaetics.dronessimulator.common;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A manageable thread which can start, stop, pause and resume
 * After stop, the thread can be started again.
 * Interrupt this thread to completely destroy it
 */
public abstract class ManagedThread extends Thread {
     /**
     * Thread management if the thread has started
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Thread management if the thread has quit
     */
    private final AtomicBoolean quit = new AtomicBoolean(false);

    /**
     * Thread management if the thread is paused
     */
    private final AtomicBoolean pauseToken = new AtomicBoolean(false);

    /**
     * Run this thread. Will start work if started is set
     * Pauses if pause is set
     * Quits if quit is set
     */
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

    /**
     * Start this thread work after the initial this.start()
     */
    public void startThread() {
        synchronized (started) {
            started.set(true);
            started.notifyAll();
        }
    }

    /**
     * Resume this thread work after a pause
     * Does nothing if thread is not paused
     */
    public void resumeThread() {
        synchronized (pauseToken) {
            pauseToken.set(false);
            pauseToken.notifyAll();
        }
    }

    /**
     * Pauses this thread work
     */
    public void pauseThread() {
        synchronized (pauseToken) {
            pauseToken.set(true);
        }
    }

    /**
     * Stops this thread work to be started again
     * Auto-resumes if thread is paused
     */
    public void stopThread() {
        synchronized (quit) {
            quit.set(true);
            this.resumeThread();
        }
    }

    /**
     * Overridable to be called upon starting thread work
     */
    protected void onStart() {}
    /**
     * Overridable to be called upon pausing thread work
     */
    protected void onPause() {}
    /**
     * Overridable to be called upon resuming thread work
     */
    protected void onResume() {}
    /**
     * Overridable to be called upon stopping thread work
     */
    protected void onStop() {}

    /**
     * Abstract method which is the work the thread will perform when started
     * @throws InterruptedException Is allowed to throw interrupt errors to interrupt the thread
     */
    protected abstract void work() throws InterruptedException;
}
