package org.inaetics.dronessimulator.test.concurrent;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentExecute {
    private final AtomicLong nextId;

    private final ArrayList<ConcurrentJobEntry> jobEntries;
    private final ExecutorService pool;
    private final TimeoutHandler timeoutHandler;


    public ConcurrentExecute(int concurrentThreads) {
        this.nextId = new AtomicLong(0);
        this.jobEntries = new ArrayList<>();
        this.pool = Executors.newFixedThreadPool(concurrentThreads);
        this.timeoutHandler = new TimeoutHandler();
    }

    public void addJob(int amount, int timeoutMs, Runnable job) {
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

        try {
            this.timeoutHandler.start();
            List<Future<Object>> futures = this.pool.invokeAll(jobs);
            awaitTermination(futures);
            this.pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                this.timeoutHandler.quit();
                this.timeoutHandler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void awaitTermination(List<Future<Object>> futures) {
        RuntimeException exceptions = new RuntimeException();
        for(Future<Object> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                exceptions.addSuppressed(e);
            }
        }

        if(exceptions.getSuppressed().length > 0) {
            throw exceptions;
        }
    }
}
