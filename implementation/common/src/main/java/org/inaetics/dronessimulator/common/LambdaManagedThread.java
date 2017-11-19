package org.inaetics.dronessimulator.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LambdaManagedThread extends ManagedThread{
    private final Runnable work;
    @Override
    protected void work() throws InterruptedException {
        work.run();
    }
}
