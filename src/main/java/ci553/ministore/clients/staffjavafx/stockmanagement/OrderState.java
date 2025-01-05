package ci553.ministore.clients.staffjavafx.stockmanagement;

/**
 * Enum representing the different states of an order in the stock management
 * interface.
 */
public enum OrderState {
    /**
     * State when the staff is entering a product number.
     */
    ENTERING_PRODUCT,

    /**
     * State when the staff is checking the stock for a product.
     */
    CHECKING_STOCK,

    /**
     * State when the staff is managing the stock.
     */
    MANAGING_STOCK,

    /**
     * State when the staff is creating a new product.
     */
    CREATING_NEW_PRODUCT,

    /**
     * State when the staff is adding an image to a product.
     */
    ADDING_IMAGE
}
