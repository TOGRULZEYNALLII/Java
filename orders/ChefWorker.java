package orders;

import orders.order;

import java.util.concurrent.BlockingQueue;

public class ChefWorker implements Runnable {

    private final BlockingQueue<order> queue;

    public ChefWorker(BlockingQueue<order> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                order order = queue.take(); // blocks if empty
                order.process();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
