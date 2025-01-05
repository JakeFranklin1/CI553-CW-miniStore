package ci553.ministore.clients.staffjavafx.packing;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.middle.OrderProcessing;
import ci553.ministore.middle.StockReadWriter;
import ci553.ministore.middle.OrderException;
import ci553.ministore.catalogue.Basket;
import ci553.ministore.debug.DEBUG;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Model class for the packing interface.
 * Handles business logic and updates the view through properties.
 */
@SuppressWarnings("unused")
public class PackingModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final MiddleFactory middleFactory;
    private final AtomicReference<Basket> theBasket = new AtomicReference<>();
    private StockReadWriter theStock = null;
    private OrderProcessing theOrder = null;
    private final StateOf worker = new StateOf();
    private volatile boolean running = true; // Flag to control the running state of the thread
    private Thread checkThread; // Reference to the background thread

    /**
     * Constructor for PackingModel.
     * Initializes the model with the provided MiddleFactory and starts a background
     * thread to check for new orders.
     *
     * @param mlf The MiddleFactory instance.
     */
    public PackingModel(MiddleFactory mlf) {
        this.middleFactory = mlf;
        try {
            theStock = mlf.makeStockReadWriter();
            theOrder = mlf.makeOrderProcessing();
            message.set("Waiting for orders...");
            reply.set("");
            // Start background thread to check for new orders
            checkThread = new Thread(this::checkForNewOrder);
            checkThread.setDaemon(true); // Make it a daemon thread
            checkThread.start();
        } catch (Exception e) {
            DEBUG.error("PackingModel::constructor\n%s", e.getMessage());
        }
    }

    /**
     * Background thread method to check for new orders.
     * Continuously checks for new orders and updates the message and reply
     * properties.
     */
    private void checkForNewOrder() {
        while (running) { // Use running flag to control the loop
            try {
                boolean isFree = worker.claim();
                if (isFree) {
                    Basket newOrder = theOrder.getOrderToPack();
                    if (newOrder != null) {
                        theBasket.set(newOrder);
                        message.set(String.format("Current Order: #%03d", newOrder.getOrderNum()));
                        reply.set(newOrder.getDetails());
                    } else {
                        worker.free();
                        message.set("Waiting for orders...");
                        reply.set("");
                    }
                }
                Thread.sleep(2000); // Sleep for 2 seconds before checking again
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break;
            } catch (Exception e) {
                DEBUG.error("PackingModel::checkForNewOrder\n%s", e.getMessage());
            }
        }
    }

    /**
     * Marks the current order as packed.
     * Updates the message and reply properties accordingly.
     */
    public void doPack() {
        try {
            Basket basket = theBasket.get();
            if (basket != null) {
                int orderNum = basket.getOrderNum();
                // Mark order as packed in the order system
                theOrder.informOrderPacked(orderNum);
                message.set(String.format("Order #%03d packed successfully", orderNum));
                reply.set(""); // Clear receipt after packing
                theBasket.set(null);
                worker.free();
            } else {
                message.set("No order to pack");
                reply.set("");
            }
        } catch (OrderException e) {
            DEBUG.error("PackingModel::doPack\n%s", e.getMessage());
            message.set("Error packing order: " + e.getMessage());
        }
    }

    /**
     * Cleans up resources and stops the background thread.
     * Prevents resource leaks by interrupting the thread.
     */
    public void cleanup() {
        running = false; // Set running flag to false to stop the loop
        if (checkThread != null) {
            checkThread.interrupt(); // Interrupt the thread
            DEBUG.trace("PackingModel::cleanup - Interrupting check thread");
        }
    }

    /**
     * Gets the message property for binding.
     *
     * @return The message property.
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Gets the reply property for binding.
     *
     * @return The reply property.
     */
    public StringProperty replyProperty() {
        return reply;
    }

    /**
     * Inner class to manage the state of the worker.
     * Ensures that only one order is processed at a time.
     */
    private class StateOf {
        private boolean held = false;

        /**
         * Claims the worker if it is free.
         *
         * @return True if the worker was free and is now claimed, false otherwise.
         */
        public synchronized boolean claim() {
            return held ? false : (held = true);
        }

        /**
         * Frees the worker, making it available for the next order.
         */
        public synchronized void free() {
            held = false;
        }
    }
}
