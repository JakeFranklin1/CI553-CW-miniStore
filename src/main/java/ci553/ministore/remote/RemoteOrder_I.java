package ci553.ministore.remote;

import ci553.ministore.catalogue.Basket;
import ci553.ministore.middle.OrderException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Defines the RMI interface for the Order object.
 * Provides methods for creating and managing orders remotely.
 * Each method throws RemoteException to handle network-related issues and
 * OrderException for order-related errors.
 * 
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
public interface RemoteOrder_I extends Remote {

    /**
     * Creates a new order.
     *
     * @param order The Basket object representing the order
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error processing the order
     */
    void newOrder(Basket order) throws RemoteException, OrderException;

    /**
     * Generates a unique order number.
     *
     * @return A unique order number
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error generating the order number
     */
    int uniqueNumber() throws RemoteException, OrderException;

    /**
     * Retrieves the next order to be packed.
     *
     * @return The Basket object representing the order to be packed
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error retrieving the order
     */
    Basket getOrderToPack() throws RemoteException, OrderException;

    /**
     * Informs the system that an order has been packed.
     *
     * @param orderNum The order number of the packed order
     * @return true if the operation was successful, false otherwise
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error updating the order status
     */
    boolean informOrderPacked(int orderNum) throws RemoteException, OrderException;

    /**
     * Informs the system that an order has been collected.
     *
     * @param orderNum The order number of the collected order
     * @return true if the operation was successful, false otherwise
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error updating the order status
     */
    boolean informOrderCollected(int orderNum) throws RemoteException, OrderException;

    /**
     * Retrieves the state of all orders.
     *
     * @return A map where the key is the order state and the value is a list of
     *         order numbers
     * @throws RemoteException If there is an RMI error
     * @throws OrderException  If there is an error retrieving the order state
     */
    Map<String, List<Integer>> getOrderState() throws RemoteException, OrderException;
}
