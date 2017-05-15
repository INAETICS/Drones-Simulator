package org.inaetics.dronessimulator.test.concurrent;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

@Getter
public class ConcurrentJobEntry {
    private final long id;
    private final int amount;
    private final int timeoutMs;
    private final TimeoutHandler timeoutHandler;
    private final Runnable job;

    public ConcurrentJobEntry(long id, int amount, int timeoutMs, TimeoutHandler timeoutHandler, Runnable job) {
        this.id = id;
        this.amount = amount;
        this.timeoutMs = timeoutMs;
        this.timeoutHandler = timeoutHandler;
        this.job = job;
    }

    Collection<ConcurrentJob> getJobs() {
        List<ConcurrentJob> result = new ArrayList<>(this.amount);

        for(int i = 0; i < this.amount; i++) {
            result.add(new ConcurrentJob(i));
        }

        return result;
    }

    @Getter
    public class ConcurrentJob implements Callable<Object> {
        private final int subid;
        private boolean hasRun;
        private boolean success;

        private Thread executingThread;

        public ConcurrentJob(int subid) {
            this.subid = subid;
            this.hasRun = false;
            this.success = false;

            this.executingThread = null;
        }

        public String getJobId() {
            return id + "." + subid;
        }

        public void run() {
            this.executingThread = Thread.currentThread();
            timeoutHandler.addTimeoutFromNow(timeoutMs, this);

            job.run();
            this.hasRun = true;
            this.success = true;
        }

        public void timeout() {
            this.hasRun = true;
            this.success = false;

            if(this.executingThread != null) {
                this.executingThread.interrupt();
            }
        }

        @Override
        public Object call() throws Exception {
            this.run();
            return null;
        }

        @Override
        public String toString() {
            return "(Job " + this.getJobId() + ")";
        }
    }
}
