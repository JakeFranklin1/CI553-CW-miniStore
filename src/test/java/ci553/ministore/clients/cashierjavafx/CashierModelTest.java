package ci553.ministore.clients.cashierjavafx;

import ci553.ministore.catalogue.Basket;
import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CashierModel.
 * Tests the business logic of the cashier operations.
 */
public class CashierModelTest {

    private CashierModel cashierModel;
    private MiddleFactory middleFactoryMock;
    private StockReadWriter stockReadWriterMock;
    private OrderProcessing orderProcessingMock;

    @BeforeEach
    void setUp() throws OrderException, StockException {
        // Create mocks
        middleFactoryMock = mock(MiddleFactory.class);
        stockReadWriterMock = mock(StockReadWriter.class);
        orderProcessingMock = mock(OrderProcessing.class);

        // Configure mocks
        when(middleFactoryMock.makeStockReadWriter()).thenReturn(stockReadWriterMock);
        when(middleFactoryMock.makeOrderProcessing()).thenReturn(orderProcessingMock);

        // Create model
        cashierModel = new CashierModel(middleFactoryMock);
    }

    @Test
    void testDoCheck_WhenProductExists() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 5);
        when(stockReadWriterMock.exists("001")).thenReturn(true);
        when(stockReadWriterMock.getDetails("001")).thenReturn(product);

        // Act
        cashierModel.doCheck("001");

        // Assert
        assertEquals("Product Number: 001\nDescription: Test Product\nPrice: £10.00\nQuantity in Stock: 5",
            cashierModel.replyProperty().get());
    }

    @Test
    void testDoCheck_WhenProductDoesNotExist() throws StockException {
        // Arrange
        when(stockReadWriterMock.exists("001")).thenReturn(false);

        // Act
        cashierModel.doCheck("001");

        // Assert
        assertEquals("Error: Unknown product number 001", cashierModel.replyProperty().get());
    }

    @Test
    void testDoCheck_WhenProductOutOfStock() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 0);
        when(stockReadWriterMock.exists("001")).thenReturn(true);
        when(stockReadWriterMock.getDetails("001")).thenReturn(product);

        // Act
        cashierModel.doCheck("001");

        // Assert
        assertEquals("Product: Test Product is currently out of stock",
            cashierModel.replyProperty().get());
    }

    @Test
    void testAddToOrder_WhenProductExistsAndInStock() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 5);
        when(stockReadWriterMock.exists("001")).thenReturn(true);
        when(stockReadWriterMock.getDetails("001")).thenReturn(product);
        when(stockReadWriterMock.buyStock("001", 1)).thenReturn(true);

        // Act
        cashierModel.addToOrder("001");

        // Assert
        assertFalse(cashierModel.getBasket().isEmpty());
        verify(stockReadWriterMock).buyStock("001", 1);
    }

    @Test
    void testPurchase_WhenBasketHasItems() throws OrderException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 1);
        cashierModel.getBasket().add(product);

        // Act
        cashierModel.purchase();

        // Assert
        verify(orderProcessingMock).newOrder(any(Basket.class));
        assertTrue(cashierModel.getBasket().isEmpty());
    }

    @Test
    void testClearBasket() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 1);
        cashierModel.getBasket().add(product);

        // Act
        cashierModel.clearBasket(true);

        // Assert
        assertTrue(cashierModel.getBasket().isEmpty());
        verify(stockReadWriterMock).addStock("001", 1);
    }

    @Test
    void testRemoveQuantityFromBasket() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 2);
        cashierModel.getBasket().add(product);

        // Act
        cashierModel.removeQuantityFromBasket("001", 1);

        // Assert
        assertEquals(1, cashierModel.getProductQuantityInBasket("001"));
        verify(stockReadWriterMock).addStock("001", 1);
    }
}
