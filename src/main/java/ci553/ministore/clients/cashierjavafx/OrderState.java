package ci553.ministore.clients.cashierjavafx;

/**
 * Enum representing the different states of an order in the cashier interface.
 */
public enum OrderState {
    /**
     * State when the cashier is entering a product number.
     */
    ENTERING_PRODUCT,

    /**
     * State when the cashier is checking the stock for a product.
     */
    CHECKING_STOCK,

    /**
     * State when the cashier is adding a product to the order.
     */
    ADDING_TO_ORDER,

    /**
     * State when the cashier is completing the purchase.
     */
    PURCHASING
}
