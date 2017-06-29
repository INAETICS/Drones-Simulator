package org.inaetics.dronessimulator.test.concurrent;

import org.apache.log4j.Logger;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * TimeoutHandler to keep track of timeouts. Each {@link org.inaetics.dronessimulator.test.concurrent.ConcurrentJobEntry.ConcurrentJob}
 * has a timeout. This handler keeps track of all timeouts and makes sure to interrupt the jobs which take too long.
 */
public class TimeoutHandler extends Thread {
    private static final Logger logger = Logger.getLogger(TimeoutHandler.class);
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

    /**
     * Try to sleep till the next timeout. If an earlier timeout is added, be interrupted and reconfigure
     * to sleep till that timeout instead.
     */
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

    /**
     * Quit a running TimeoutHandler
     */
    public void quit() {
        this.quit = true;
        this.interrupt();
    }

    /**
     * A single timeout entry to be handled by the timeout handler.
     */
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

        @Override
        public boolean equals(Object o) {
            return o instanceof TimeoutEntry && this.timeoutAt.equals(((TimeoutEntry) o).getTimeoutAt());
        }

        @Override
        public int hashCode() {
            return this.timeoutAt.intValue();
        }
    }
}
