package org.inaetics.dronessimulator.test.concurrent;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConcurrentJobEntry {
    private final long id;
    private final int amount;
    private final int timeoutMs;
    private final TimeoutHandler timeoutHandler;
    private final IConcurrentJob job;

    public ConcurrentJobEntry(long id, int amount, int timeoutMs, TimeoutHandler timeoutHandler, IConcurrentJob job) {
        this.id = id;
        this.amount = amount;
        this.timeoutMs = timeoutMs;
        this.timeoutHandler = timeoutHandler;
        this.job = job;
    }

    public long getId() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getTimeoutMs() {
        return this.timeoutMs;
    }

    public TimeoutHandler getTimeoutHandler() {
        return this.timeoutHandler;
    }

    public IConcurrentJob getJob() {
        return this.job;
    }

    Collection<ConcurrentJob> getJobs() {
        List<ConcurrentJob> result = new ArrayList<>(this.amount);

        for(int i = 0; i < this.amount; i++) {
            result.add(new ConcurrentJob(i));
        }

        return result;
    }

    public class ConcurrentJob implements Runnable {
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

        public boolean isHasRun() {
            return this.hasRun;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public void run() {
            this.executingThread = Thread.currentThread();
            timeoutHandler.addTimeoutFromNow(timeoutMs, this);

            job.run(subid);
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
        public String toString() {
            return "(Job " + this.getJobId() + ")";
        }
    }
}
