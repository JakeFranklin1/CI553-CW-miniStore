package clients.staffjavafx.packing;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;
import middle.OrderException;
import catalogue.Basket;
import debug.DEBUG;

import java.util.concurrent.atomic.AtomicReference;

public class PackingModel {
    private final StringProperty message = new SimpleStringProperty();
    private final StringProperty reply = new SimpleStringProperty();
    private final MiddleFactory middleFactory;
    private final AtomicReference<Basket> theBasket = new AtomicReference<>();
    private StockReadWriter theStock = null;
    private OrderProcessing theOrder = null;
    private final StateOf worker = new StateOf();
    private volatile boolean running = true; // Add this flag
    private Thread checkThread; // Add thread reference

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

    private void checkForNewOrder() {
        while (running) { // Use running flag
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                DEBUG.error("PackingModel::checkForNewOrder\n%s", e.getMessage());
            }
        }
    }

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

    // Prevent resource leak (found this out the hard way)
    public void cleanup() {
        running = false;
        if (checkThread != null) {
            checkThread.interrupt();
            DEBUG.trace("PackingModel::cleanup - Interrupting check thread");
        }
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty replyProperty() {
        return reply;
    }

    private class StateOf {
        private boolean held = false;

        public synchronized boolean claim() {
            return held ? false : (held = true);
        }

        public synchronized void free() {
            held = false;
        }
    }
}
