package orders;

import orders.order;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderGenerator implements Runnable {

    private final BlockingQueue<order> queue;
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final String[] foods = {"Pizza", "Burger", "Pasta", "Salad", "Sushi"};
    private final Random random = new Random();

    public OrderGenerator(BlockingQueue<order> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String food = foods[random.nextInt(foods.length)];
                order order = new order(idGenerator.getAndIncrement(), food);

                queue.put(order);
                System.out.println("New Order Added: #" + order.getId() +
                        " - " + order.getDescription());

                Thread.sleep(1500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
