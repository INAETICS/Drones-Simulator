package org.inaetics.dronessimulator.test.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Test class to perform a number of jobs a specified amount of times
 * The jobs are shuffled and performed concurrently by a threadpool
 */
public class ConcurrentExecute {
    /**
     * Next unique id
     */
    private final AtomicLong nextId;

    /**
     * The job templates to be performed
     */
    private final ArrayList<ConcurrentJobEntry> jobEntries;

    /**
     * The pool to perform the jobs
     */
    private final ThreadPoolExecutor pool;
    /**
     * TimeoutHandler to make sure jobs are interrupted when they take too long
     */
    private final TimeoutHandler timeoutHandler;

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ConcurrentExecute.class);
    /**
     * Create the ConcurrentExecute to perform a number of jobs concurrently
     * @param concurrentThreads The amount of threads to concurrently perform the job
     */
    public ConcurrentExecute(int concurrentThreads) {
        this.nextId = new AtomicLong(0);
        this.jobEntries = new ArrayList<>();
        this.pool = new ThreadPoolExecutor(concurrentThreads, concurrentThreads, 1, TimeUnit.DAYS, new LinkedBlockingQueue<>());
        this.timeoutHandler = new TimeoutHandler();
    }

    /**
     * Add a job to be performed a number of times
     * @param amount How many times this job should be performed
     * @param timeoutMs
     * @param job A lambda function which follows the {@link IConcurrentJob} interface. Should have a function which takes one argument int i which represents which time this job is performed
     */
    public void addJob(int amount, int timeoutMs, IConcurrentJob job) {
        long id = nextId.incrementAndGet();
        synchronized (this) {
            this.jobEntries.add(new ConcurrentJobEntry(id, amount, timeoutMs, this.timeoutHandler, job));
        }
    }

    /**
     * Start the threadpool to perform all jobs. Jobs are randomly shuffled to increase concurrency risks.
     */
    public void start() {
        List<ConcurrentJobEntry.ConcurrentJob> jobs = new ArrayList<>();

        synchronized (this) {
            for(ConcurrentJobEntry jobEntry : jobEntries) {
                jobs.addAll(jobEntry.getJobs());
            }
        }

        Collections.shuffle(jobs);

        this.timeoutHandler.start();

        for(ConcurrentJobEntry.ConcurrentJob job : jobs) {
            this.pool.execute(job);
        }
    }

    /**
     * Blocking wait till all jobs are performed.
     */
    public void waitTillDone() {
        try {
            this.pool.shutdown();
            this.pool.awaitTermination(10, TimeUnit.DAYS);
            this.timeoutHandler.quit();
            this.timeoutHandler.join();
        } catch (InterruptedException e) {
            log.fatal(e);
            Thread.currentThread().interrupt();
        }
    }
}
