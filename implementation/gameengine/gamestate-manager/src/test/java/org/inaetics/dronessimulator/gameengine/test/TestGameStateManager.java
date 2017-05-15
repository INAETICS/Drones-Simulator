package org.inaetics.dronessimulator.gameengine.test;


import org.inaetics.dronessimulator.test.concurrent.ConcurrentExecute;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class TestGameStateManager {

    @Test
    @Category(value = GameEngineTests.class)
    public void testAdd() {
        int test = 0;
        ConcurrentExecute execute = new ConcurrentExecute(10);

        execute.addJob(10, 100, () -> System.out.println(test + 1));
        execute.addJob(10, 100, () -> System.out.println(test + 2));
        execute.addJob(10, 100, () -> System.out.println("3"));
        execute.addJob(10, 100, () -> System.out.println("4"));
        execute.addJob(10, 100, () -> System.out.println("5"));
        execute.addJob(10, 50, () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        execute.start();


    }

}
