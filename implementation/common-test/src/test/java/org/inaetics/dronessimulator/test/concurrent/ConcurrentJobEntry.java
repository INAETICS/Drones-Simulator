package org.inaetics.dronessimulator.test.concurrent;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Template of a ConcurrentJob
 * It is used by {@link ConcurrentExecute} to keep record of which jobs to perform and how many times they should
 * be performed.
 */
public class ConcurrentJobEntry {
    /**
     * The id of the template. This is shared by all amount times invocations of this job
     */
    private final long id;
    /**
     * How many times this job needs to be performed
     */
    private final int amount;
    /**
     * How long each separate invocation may take in milliseconds
     */
    private final int timeoutMs;
    /**
     * Which TimeoutHandler to register the timeout of each job with
     */
    private final TimeoutHandler timeoutHandler;
    /**
     * The job to be performed. Should follow {@link IConcurrentJob} structure
     */
    private final IConcurrentJob job;

    /**
     * Construct a job entry for ConcurrentExecute
     * @param id The id of this template
     * @param amount Amount of times to perform job
     * @param timeoutMs How long each job invocation may take
     * @param timeoutHandler Handler to register the timeout of each invocation at
     * @param job The job to be performed. Should follow {@link IConcurrentJob} structure. Usually a lambda with
     * a single argument int i. This argument is amount of time this job has performed. E.g. 1 if this is the second
     * time the job is performed. Due to shuffle, i is not performed in order. In other words: i=2  may be before i=1.
     */
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

    /**
     * Gets all specific jobs based on this template.
     * @return List of jobs to be performed by execute. They each contain their subid which is used for the argument
     * of the job
     */
    Collection<ConcurrentJob> getJobs() {
        List<ConcurrentJob> result = new ArrayList<>(this.amount);

        for(int i = 0; i < this.amount; i++) {
            result.add(new ConcurrentJob(i));
        }

        return result;
    }

    /**
     * While the outer class {@link ConcurrentJobEntry} is a template to be instantiated amount times, this class represents
     * a single instance of the template. In other words, this is a specific job while the ConcurrentJobEntry is a template
     * of a job.
     */
    public class ConcurrentJob implements Runnable {
        /**
         * The subid of this specific job
         */
        private final int subid;
        /**
         * If this job has run
         */
        private boolean hasRun;
        /**
         * If this job was a success
         */
        private boolean success;

        /**
         * Which thread executes this job
         */
        private Thread executingThread;

        /**
         * Create a specific job
         * @param subid The subid of this job
         */
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

        /**
         * Run the job. Should not be called directly as this is a Thread!
         */
        public void run() {
            this.executingThread = Thread.currentThread();
            timeoutHandler.addTimeoutFromNow(timeoutMs, this);

            job.run(subid);
            this.hasRun = true;
            this.success = true;
        }

        /**
         * Timeout this job
         */
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
