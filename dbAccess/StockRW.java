// filepath: /c:/Users/jakef/OneDrive/Desktop/project/CI553-CW-miniStore/dbAccess/StockRW.java
package dbAccess;

import catalogue.Product;
import debug.DEBUG;
import middle.StockException;
import middle.StockReadWriter;

import java.sql.SQLException;

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
                            "             stockLevel >= " + amount + ""
            );
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
                            "         where productNo = '" + pNum + "'"
            );
            DEBUG.trace("DB StockRW: addStock(%s,%d)", pNum, amount);
        } catch (SQLException e) {
            throw new StockException("SQL addStock: " + e.getMessage());
        }
    }

    public synchronized void modifyStock(Product detail) throws StockException {
        DEBUG.trace("DB StockRW: modifyStock(%s)",
                detail.getProductNum());
        try {
            if (!exists(detail.getProductNum())) {
                getStatementObject().executeUpdate(
                        "insert into ProductTable values ('" +
                                detail.getProductNum() + "', " +
                                "'" + detail.getDescription() + "', " +
                                "'images/Pic" + detail.getProductNum() + ".jpg', " +
                                "'" + detail.getPrice() + "' " + ")"
                );
                getStatementObject().executeUpdate(
                        "insert into StockTable values ('" +
                                detail.getProductNum() + "', " +
                                "'" + detail.getQuantity() + "' " + ")"
                );
            } else {
                getStatementObject().executeUpdate(
                        "update ProductTable " +
                                "  set description = '" + detail.getDescription() + "' , " +
                                "      price       = " + detail.getPrice() +
                                "  where productNo = '" + detail.getProductNum() + "' "
                );

                getStatementObject().executeUpdate(
                        "update StockTable set stockLevel = " + detail.getQuantity() +
                                "  where productNo = '" + detail.getProductNum() + "'"
                );
            }
        } catch (SQLException e) {
            throw new StockException("SQL modifyStock: " + e.getMessage());
        }
    }
}
