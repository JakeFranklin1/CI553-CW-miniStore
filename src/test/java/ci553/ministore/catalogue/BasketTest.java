package ci553.ministore.catalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BetterBasket Tests")
public class BasketTest {

    private BetterBasket basket;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        basket = new BetterBasket();
        product1 = new Product("001", "Product 1", 10.0, 2);
        product2 = new Product("002", "Product 2", 20.0, 1);
    }

    @Test
    @DisplayName("Should auto-increment order numbers")
    void testOrderNumberAutoIncrement() {
        // Arrange
        BetterBasket basket1 = new BetterBasket();
        BetterBasket basket2 = new BetterBasket();

        // Act & Assert
        assertNotEquals(basket1.getOrderNum(), basket2.getOrderNum());
        assertTrue(basket2.getOrderNum() > basket1.getOrderNum());
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
        basket.add(product1); // Quantity: 2
        basket.add(new Product("001", "Product 1", 10.0, 3)); // Additional quantity: 3

        // Act
        int quantity = basket.getProductQuantity("001");

        // Assert
        assertEquals(5, quantity);
    }

    @Test
    @DisplayName("Should remove specific quantity from product")
    void testRemoveQuantityByProductNum() {
        // Arrange
        basket.add(product1); // Quantity: 2
        basket.add(new Product("001",
                                "Product 1",
                                10.0,
                                3));  // Total quantity: 5

        // Act
        basket.removeQuantityByProductNum("001", 3);

        // Assert
        assertEquals(2, basket.getProductQuantity("001"));
    }
}
