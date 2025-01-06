package ci553.ministore.clients.customerjavafx;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.MiddleFactory;
import ci553.ministore.middle.StockReader;
import ci553.ministore.middle.StockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Customer Model Tests")
class CustomerModelTest {

    private CustomerModel customerModel;
    private MiddleFactory middleFactoryMock;
    private StockReader stockReaderMock;

    @BeforeEach
    void setUp() throws StockException {
        // Create mocks
        middleFactoryMock = mock(MiddleFactory.class);
        stockReaderMock = mock(StockReader.class);

        // Configure mock to return StockReader
        when(middleFactoryMock.makeStockReader()).thenReturn(stockReaderMock);

        // Create model with mock factory
        customerModel = new CustomerModel(middleFactoryMock);
    }

    @Test
    @DisplayName("Should display product details when product exists and is in stock")
    void testDoCheck_WhenProductExistsAndInStock() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 10);
        when(stockReaderMock.exists("001")).thenReturn(true);
        when(stockReaderMock.getDetails("001")).thenReturn(product);
        when(stockReaderMock.getImage("001")).thenReturn(new byte[0]);

        // Act
        customerModel.doCheck("001");

        // Assert
        String expected = String.format("""
                Product Number: 001
                Description: Test Product
                Price: £%.2f
                Quantity in Stock: %d""",
                product.getPrice(),
                product.getQuantity());
        assertEquals(expected, customerModel.getReply());
    }

    @Test
    @DisplayName("Should display low stock warning when stock level is below 5")
    void testDoCheck_WhenProductHasLowStock() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 3);
        when(stockReaderMock.exists("001")).thenReturn(true);
        when(stockReaderMock.getDetails("001")).thenReturn(product);

        // Act
        customerModel.doCheck("001");

        // Assert
        assertTrue(customerModel.getReply().contains("Warning: Low Stock!"));
    }

    @Test
    @DisplayName("Should display out of stock message when product has zero stock")
    void testDoCheck_WhenProductOutOfStock() throws StockException {
        // Arrange
        Product product = new Product("001", "Test Product", 10.0, 0);
        when(stockReaderMock.exists("001")).thenReturn(true);
        when(stockReaderMock.getDetails("001")).thenReturn(product);

        // Act
        customerModel.doCheck("001");

        // Assert
        assertEquals("Product: Test Product is currently out of stock",
            customerModel.getReply());
    }

    @Test
    @DisplayName("Should display error message when product does not exist")
    void testDoCheck_WhenProductDoesNotExist() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(false);

        // Act
        customerModel.doCheck("001");

        // Assert
        assertEquals("Error: Unknown product number 001",
            customerModel.getReply());
    }

    @Test
    @DisplayName("Should handle StockException gracefully")
    void testDoCheck_WhenStockExceptionOccurs() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001"))
            .thenThrow(new StockException("Database error"));

        // Act
        customerModel.doCheck("001");

        // Assert
        assertEquals("System Error: Database error",
            customerModel.getReply());
    }

    @Test
    @DisplayName("Should clear product image when product does not exist")
    void testDoCheck_ImageClearedWhenProductNotFound() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(false);

        // Act
        customerModel.doCheck("001");

        // Assert
        assertNull(customerModel.getProductImage());
    }
}
