// filepath: /c:/Users/jakef/OneDrive/Desktop/project/CI553-CW-miniStore/dbAccess/StockRW.java
package dbAccess;

import catalogue.Product;
import debug.DEBUG;
import middle.StockException;
import middle.StockReadWriter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;

public class StockRW extends StockR implements StockReadWriter {
    public StockRW() throws StockException {
        super();
    }

    public synchronized boolean buyStock(String pNum, int amount) throws StockException {
        DEBUG.trace("DB StockRW: buyStock(%s,%d)", pNum, amount);
        int updates = 0;
        try {
            getStatementObject().executeUpdate(
                    "update StockTable set stockLevel = stockLevel-" + amount +
                            "       where productNo = '" + pNum + "' and " +
                            "             stockLevel >= " + amount + "");
            updates = 1;
        } catch (SQLException e) {
            throw new StockException("SQL buyStock: " + e.getMessage());
        }
        DEBUG.trace("buyStock() updates -> %n", updates);
        return updates > 0;
    }

    public synchronized void addStock(String pNum, int amount) throws StockException {
        try {
            getStatementObject().executeUpdate(
                    "update StockTable set stockLevel = stockLevel + " + amount +
                            "         where productNo = '" + pNum + "'");
            DEBUG.trace("DB StockRW: addStock(%s,%d)", pNum, amount);
        } catch (SQLException e) {
            throw new StockException("SQL addStock: " + e.getMessage());
        }
    }

    public synchronized void modifyStock(Product detail) throws StockException {
        DEBUG.trace("DB StockRW: modifyStock(%s)", detail.getProductNum());
        try {
            if (!exists(detail.getProductNum())) {
                getStatementObject().executeUpdate(
                        "insert into ProductTable values ('" +
                                detail.getProductNum() + "', " +
                                "'" + detail.getDescription() + "', " +
                                "'images/placeholder.png', " +
                                detail.getPrice() + ")");
                getStatementObject().executeUpdate(
                        "insert into StockTable values ('" +
                                detail.getProductNum() + "', " +
                                detail.getQuantity() + ")");
            } else {
                getStatementObject().executeUpdate(
                        "update ProductTable " +
                                "  set description = '" + detail.getDescription() + "' , " +
                                "      price       = " + detail.getPrice() +
                                "  where productNo = '" + detail.getProductNum() + "' ");

                getStatementObject().executeUpdate(
                        "update StockTable set stockLevel = " + detail.getQuantity() +
                                "  where productNo = '" + detail.getProductNum() + "'");
            }
        } catch (SQLException e) {
            throw new StockException("SQL modifyStock: " + e.getMessage());
        }
    }

    @Override
    public synchronized void setStock(String productNum, int quantity) throws StockException {
        try {
            Product product = getDetails(productNum);
            product.setQuantity(quantity);
            modifyStock(product);
        } catch (StockException e) {
            throw new StockException("Error setting stock: " + e.getMessage());
        }
    }

    @Override
    public synchronized void addProduct(Product product) throws StockException {
        DEBUG.trace("DB StockRW: addProduct(%s)", product.getProductNum());
        try {
            if (!exists(product.getProductNum())) {
                getStatementObject().executeUpdate(
                        "insert into ProductTable values ('" +
                                product.getProductNum() + "', " +
                                "'" + product.getDescription() + "', " +
                                "'images/Pic" + product.getProductNum() + ".png', " +
                                "'" + product.getPrice() + "' " + ")");
                getStatementObject().executeUpdate(
                        "insert into StockTable values ('" +
                                product.getProductNum() + "', " +
                                "'" + product.getQuantity() + "' " + ")");
            } else {
                throw new StockException("Product already exists: " + product.getProductNum());
            }
        } catch (SQLException e) {
            throw new StockException("SQL addProduct: " + e.getMessage());
        }
    }

    @Override
    public synchronized List<Product> getProducts() throws StockException {
        List<Product> products = new ArrayList<>();
        try {
            ResultSet rs = getStatementObject().executeQuery(
                    "SELECT ProductTable.productNo, ProductTable.description, ProductTable.price, StockTable.stockLevel "
                            +
                            "FROM ProductTable " +
                            "JOIN StockTable ON ProductTable.productNo = StockTable.productNo");
            while (rs.next()) {
                String productNum = rs.getString("productNo");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("stockLevel");
                products.add(new Product(productNum, description, price, quantity));
            }
            rs.close();
        } catch (SQLException e) {
            throw new StockException("SQL getProducts: " + e.getMessage());
        }
        return products;
    }

    public synchronized void updateProductImage(String productNum, String imagePath) throws StockException {
        DEBUG.trace("DB StockRW: updateProductImage(%s, %s)", productNum, imagePath);
        try {
            getStatementObject().executeUpdate(
                    "UPDATE ProductTable SET picture = '" + imagePath +
                            "' WHERE productNo = '" + productNum + "'");
        } catch (SQLException e) {
            throw new StockException("SQL updateProductImage: " + e.getMessage());
        }
    }

    @Override
    public synchronized void deleteProduct(String productNum) throws StockException {
        DEBUG.trace("DB StockRW: deleteProduct(%s)", productNum);
        try {
            getStatementObject().executeUpdate(
                    "DELETE FROM StockTable WHERE productNo = '" + productNum + "'");
            getStatementObject().executeUpdate(
                    "DELETE FROM ProductTable WHERE productNo = '" + productNum + "'");
        } catch (SQLException e) {
            throw new StockException("SQL deleteProduct: " + e.getMessage());
        }
    }
}
