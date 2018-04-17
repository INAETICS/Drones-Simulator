package org.inaetics.dronessimulator.common;

public class LambdaManagedThread extends ManagedThread{
    private final Runnable work;

    public LambdaManagedThread(Runnable work) {
        this.work = work;
    }

    @Override
    protected void work() throws InterruptedException {
        work.run();
    }
}
