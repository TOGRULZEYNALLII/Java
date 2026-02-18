package orders;




public class order {
    private final int id;
    private final String description;

    public order(int id, String description) {
        this.id = id;
        this.description = description;
    }
    public void process() {
        try {
            System.out.println(Thread.currentThread().getName() +
                    " is cooking Order #" + id + " - " + description);
            Thread.sleep((1000));
            System.out.println("Order #" + id + " finished!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }


}
