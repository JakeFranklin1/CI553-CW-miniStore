package ci553.ministore.catalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Basket.
 * Tests the business logic of the basket operations.
 */
@DisplayName("Basket Tests")
public class BasketTest {

    private Basket basket;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        product1 = new Product("001", "Product 1", 10.0, 2);
        product2 = new Product("002", "Product 2", 20.0, 1);
    }

    @Test
    @DisplayName("Should add product to basket")
    void testAddProduct() {
        // Act
        boolean result = basket.add(product1);

        // Assert
        assertTrue(result);
        assertEquals(1, basket.size());
        assertTrue(basket.contains(product1));
    }

    @Test
    @DisplayName("Should remove last product from basket")
    void testRemoveLastItem() {
        // Arrange
        basket.add(product1);
        basket.add(product2);

        // Act
        basket.removeLastItem();

        // Assert
        assertEquals(1, basket.size());
        assertTrue(basket.contains(product1));
        assertFalse(basket.contains(product2));
    }

    @Test
    @DisplayName("Should remove product by product number")
    void testRemoveByProductNum() {
        // Arrange
        basket.add(product1);
        basket.add(product2);

        // Act
        basket.removeByProductNum("001");

        // Assert
        assertEquals(1, basket.size());
        assertFalse(basket.contains(product1));
        assertTrue(basket.contains(product2));
    }

    @Test
    @DisplayName("Should get correct product quantity")
    void testGetProductQuantity() {
        // Arrange
        basket.add(product1);  // Quantity: 2
        basket.add(new Product("001", "Product 1", 10.0, 3));  // Additional quantity: 3

        // Act
        int quantity = basket.getProductQuantity("001");

        // Assert
        assertEquals(5, quantity);
    }

    @Test
    @DisplayName("Should remove specific quantity from product")
    void testRemoveQuantityByProductNum() {
        // Arrange
        basket.add(product1);  // Quantity: 2

        // Act
        basket.removeQuantityByProductNum("001", 1);

        // Assert
        assertEquals(1, basket.getProductQuantity("001"));
    }

    @Test
    @DisplayName("Should handle order numbers correctly")
    void testOrderNumbers() {
        // Arrange
        int initialNum = Basket.getNextOrderNumber();

        // Act
        basket.setOrderNum(initialNum);
        Basket.incrementOrderNumber();

        // Assert
        assertEquals(initialNum, basket.getOrderNum());
        assertEquals(initialNum + 1, Basket.getNextOrderNumber());
    }
}
