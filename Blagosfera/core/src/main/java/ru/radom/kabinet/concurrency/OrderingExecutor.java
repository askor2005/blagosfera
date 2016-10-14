package ru.radom.kabinet.concurrency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * This Executor warrants task ordering for tasks with same key (key have to implement hashCode and equal methods correctly).
 * http://stackoverflow.com/questions/2153663/controlling-task-execution-order-with-executorservice
 */
public class OrderingExecutor implements Executor{

    // Executor тасков
    private final Executor delegate;
    // Очереди тасков по ключу
    private final Map<Object, Queue<Runnable>> keyedTasks = new HashMap<>();

    public OrderingExecutor(Executor delegate){
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable task) {
        // task without key can be executed immediately
        delegate.execute(task);
    }

    public void execute(Runnable task, Object key) {
        if (key == null){ // if key is null, execute without ordering
            execute(task);
            return;
        }

        boolean first;
        Runnable wrappedTask;
        synchronized (keyedTasks){
            Queue<Runnable> dependencyQueue = keyedTasks.get(key);
            first = (dependencyQueue == null);
            if (dependencyQueue == null){
                dependencyQueue = new LinkedList<>();
                keyedTasks.put(key, dependencyQueue);
            }

            wrappedTask = wrap(task, dependencyQueue, key);
            if (!first) {
                dependencyQueue.add(wrappedTask);
            }
        }

        // execute method can block, call it outside synchronize block
        if (first) {
            delegate.execute(wrappedTask);
        }
    }

    private Runnable wrap(Runnable task, Queue<Runnable> dependencyQueue, Object key) {
        return new OrderedTask(task, dependencyQueue, key);
    }

    class OrderedTask implements Runnable{

        private final Queue<Runnable> dependencyQueue;
        private final Runnable task;
        private final Object key;

        public OrderedTask(Runnable task, Queue<Runnable> dependencyQueue, Object key) {
            this.task = task;
            this.dependencyQueue = dependencyQueue;
            this.key = key;
        }

        @Override
        public void run() {
            try{
                task.run();
            } finally {
                Runnable nextTask = null;
                synchronized (keyedTasks){
                    if (dependencyQueue.isEmpty()){
                        keyedTasks.remove(key);
                    }else{
                        nextTask = dependencyQueue.poll();
                    }
                }
                if (nextTask!=null) {
                    delegate.execute(nextTask);
                }
            }
        }
    }
}