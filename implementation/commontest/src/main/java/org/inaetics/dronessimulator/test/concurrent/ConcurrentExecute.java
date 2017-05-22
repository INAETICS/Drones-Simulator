package org.inaetics.dronessimulator.test.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentExecute {
    private final AtomicLong nextId;

    private final ArrayList<ConcurrentJobEntry> jobEntries;
    private final ThreadPoolExecutor pool;
    private final TimeoutHandler timeoutHandler;


    public ConcurrentExecute(int concurrentThreads) {
        this.nextId = new AtomicLong(0);
        this.jobEntries = new ArrayList<>();
        this.pool = new ThreadPoolExecutor(concurrentThreads, concurrentThreads, 1, TimeUnit.DAYS, new LinkedBlockingQueue<>());
        this.timeoutHandler = new TimeoutHandler();
    }

    public void addJob(int amount, int timeoutMs, IConcurrentJob job) {
        long id = nextId.incrementAndGet();
        synchronized (this) {
            this.jobEntries.add(new ConcurrentJobEntry(id, amount, timeoutMs, this.timeoutHandler, job));
        }
    }


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

    public void waitTillDone() {
        try {
            this.pool.shutdown();
            this.pool.awaitTermination(10, TimeUnit.DAYS);
            this.timeoutHandler.quit();
            this.timeoutHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
