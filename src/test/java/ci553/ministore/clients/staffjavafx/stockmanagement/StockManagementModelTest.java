package ci553.ministore.clients.staffjavafx.stockmanagement;

import ci553.ministore.catalogue.Product;
import ci553.ministore.middle.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

@DisplayName("Stock Management Model Tests")
class StockManagementModelTest {

    private StockManagementModel model;
    private MiddleFactory middleFactoryMock;
    private StockReadWriter stockReaderMock;
    private Product testProduct;

    @BeforeEach
    void setUp() throws StockException {
        // Create mocks
        middleFactoryMock = mock(MiddleFactory.class);
        stockReaderMock = mock(StockReadWriter.class);
        testProduct = new Product("001", "Test Product", 10.0, 5);

        // Configure mock factory
        when(middleFactoryMock.makeStockReadWriter()).thenReturn(stockReaderMock);

        // Initialize model
        model = new StockManagementModel(middleFactoryMock);
    }

    @Test
    @DisplayName("Should check product details successfully")
    void testDoCheck_ProductExists() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(true);
        when(stockReaderMock.getDetails("001")).thenReturn(testProduct);
        when(stockReaderMock.getImage("001")).thenReturn(new byte[0]);

        // Act
        model.doCheck("001");

        // Assert
        String expectedReply = String.format("""
                Product Number: 001
                Description: Test Product
                Price: £%.2f
                Quantity in Stock: %d""",
                testProduct.getPrice(),
                testProduct.getQuantity());
        assertEquals(expectedReply, model.replyProperty().get());
    }

    @Test
    @DisplayName("Should add stock successfully")
    void testDoAdd_Success() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(true);
        when(stockReaderMock.getDetails("001"))
            .thenReturn(testProduct)
            .thenReturn(new Product("001", "Test Product", 10.0, 6));

        // Act
        model.setCurrentQuantity(1);
        model.doAdd("001");

        // Assert
        verify(stockReaderMock).addStock("001", 1);
        assertTrue(model.replyProperty().get().contains("Added 1 units to stock"));
    }

    @Test
    @DisplayName("Should correct stock level successfully")
    void testDoCorrectStock_Success() throws StockException {
        // Arrange
        Product updatedProduct = new Product("001", "Test Product", 10.0, 10);
        when(stockReaderMock.getDetails("001")).thenReturn(updatedProduct);

        // Act
        model.doCorrectStock("001", 10);

        // Assert
        verify(stockReaderMock).setStock("001", 10);
        assertTrue(model.replyProperty().get().contains("Corrected stock for product"));
    }

    @Test
    @DisplayName("Should create new product successfully")
    void testDoNewProduct_Success() throws StockException {
        // Arrange
        when(stockReaderMock.getProducts())
            .thenReturn(Arrays.asList(new Product("001", "Existing Product", 5.0, 1)));

        // Act
        model.doNewProduct("New Product", 15.0, 5);

        // Assert
        verify(stockReaderMock).modifyStock(any(Product.class));
        assertTrue(model.replyProperty().get().contains("Added new product"));
    }

    @Test
    @DisplayName("Should handle product deletion successfully")
    void testDoDeleteProduct_Success() throws StockException {
        // Arrange
        String productNum = "001";

        // Act
        model.doDeleteProduct(productNum);

        // Assert
        verify(stockReaderMock).deleteProduct(productNum);
        assertEquals("Deleted product: 001", model.replyProperty().get());
    }

    @Test
    @DisplayName("Should handle StockException gracefully")
    void testHandleStockException() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001"))
            .thenThrow(new StockException("Database error"));

        // Act
        model.doCheck("001");

        // Assert
        assertEquals("System Error: Database error", model.replyProperty().get());
        assertNull(model.getProductImage());
    }

    @Test
    @DisplayName("Should validate product successfully")
    void testValidateProduct_Success() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(true);

        // Act
        boolean result = model.validateProduct("001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle invalid product validation")
    void testValidateProduct_Invalid() throws StockException {
        // Arrange
        when(stockReaderMock.exists("001")).thenReturn(false);

        // Act
        boolean result = model.validateProduct("001");

        // Assert
        assertFalse(result);
        assertTrue(model.replyProperty().get().contains("Error: Unknown product number"));
    }
}
