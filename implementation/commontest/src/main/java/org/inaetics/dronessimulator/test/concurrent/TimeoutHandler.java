package org.inaetics.dronessimulator.test.concurrent;

import java.util.concurrent.PriorityBlockingQueue;

public class TimeoutHandler extends Thread {
    private volatile boolean quit;
    private final PriorityBlockingQueue<TimeoutEntry> timeouts;

    public TimeoutHandler() {
        this.quit = false;
        this.timeouts = new PriorityBlockingQueue<>();
    }

    public void addTimeoutFromNow(int timeoutMs, ConcurrentJobEntry.ConcurrentJob job) {
        synchronized (this) {
            TimeoutEntry newTimeout = new TimeoutEntry(System.currentTimeMillis() + timeoutMs, job);

            if(!this.timeouts.isEmpty()) {
                TimeoutEntry firstAt = this.timeouts.peek();

                if(newTimeout.getTimeoutAt() < firstAt.getTimeoutAt()) {
                    // New is earlier than run thread is waiting on
                    this.interrupt();
                }
            }

            this.timeouts.add(newTimeout);
        }
    }

    @Override
    public void run() {
        while(!quit || !timeouts.isEmpty()) {
            TimeoutEntry next;
            synchronized (this) {
                next = timeouts.poll();
            }

            if(next != null) {
                long nextAt = next.getTimeoutAt();
                long currentTime = System.currentTimeMillis();

                try {
                    Thread.sleep(Math.max(0, nextAt - currentTime));
                } catch (InterruptedException ignored) {
                    // Redo loop as first has changed. Add current one back to list
                    this.timeouts.add(next);
                    continue;
                }

                ConcurrentJobEntry.ConcurrentJob job = next.getJob();

                if(!job.isHasRun()) {
                    job.timeout();
                }
            } else {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ignored) {
                    // Redo loop as first has changed or quit has called
                }
            }
        }

    }

    public void quit() {
        this.quit = true;
        this.interrupt();
    }

    private class TimeoutEntry implements Comparable<TimeoutEntry> {
        private final Long timeoutAt;
        private final ConcurrentJobEntry.ConcurrentJob job;

        public TimeoutEntry(Long timeoutAt, ConcurrentJobEntry.ConcurrentJob job) {
            this.timeoutAt = timeoutAt;
            this.job = job;
        }

        public Long getTimeoutAt() {
            return this.timeoutAt;
        }

        public ConcurrentJobEntry.ConcurrentJob getJob() {
            return this.job;
        }

        @Override
        public int compareTo(TimeoutEntry o) {
            return this.timeoutAt.compareTo(o.getTimeoutAt());
        }
    }
}
