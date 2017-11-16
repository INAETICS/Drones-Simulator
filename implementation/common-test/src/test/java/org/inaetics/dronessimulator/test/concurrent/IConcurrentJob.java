package org.inaetics.dronessimulator.test.concurrent;

/**
 * Structure of the functions added a job to {@link ConcurrentExecute}.
 * You can add a lambda with a single argument int i. Java will turn that
 * lambda into a function following the run method.
 */
 @FunctionalInterface
public interface IConcurrentJob {
    /**
     * Run the job
     * @param i The subid of the job. If a job should be performed 5 times, the job function will be called with
     * arguments ranging from 0..4. i can be used to keep track of ids between invocations. As jobs are shuffled
     * by {@link ConcurrentExecute}, the order in which jobs with i is executed does not have to be in order.
     */
    void run(int i);
}
