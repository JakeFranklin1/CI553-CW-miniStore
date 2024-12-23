package dbAccess;

import catalogue.Product;
import debug.DEBUG;
import middle.StockException;
import middle.StockReader;

import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class StockR implements StockReader {
    private Connection theCon = null;
    private Statement theStmt = null;

    public StockR() throws StockException {
        try {
            DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();

            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password());

            theStmt = theCon.createStatement();
            theCon.setAutoCommit(true);
        } catch (SQLException e) {
            throw new StockException("SQL problem:" + e.getMessage());
        } catch (Exception e) {
            throw new StockException("Can not load database driver.");
        }
    }

    protected Statement getStatementObject() {
        return theStmt;
    }

    protected Connection getConnectionObject() {
        return theCon;
    }

    public synchronized boolean exists(String pNum) throws StockException {
        try {
            ResultSet rs = getStatementObject().executeQuery(
                    "select price from ProductTable " +
                            "  where  ProductTable.productNo = '" + pNum + "'"
            );
            boolean res = rs.next();
            DEBUG.trace("DB StockR: exists(%s) -> %s",
                    pNum, (res ? "T" : "F"));
            return res;
        } catch (SQLException e) {
            throw new StockException("SQL exists: " + e.getMessage());
        }
    }

    public synchronized Product getDetails(String pNum) throws StockException {
        try {
            Product dt = new Product("0", "", 0.00, 0);
            ResultSet rs = getStatementObject().executeQuery(
                    "select description, price, stockLevel " +
                            "  from ProductTable, StockTable " +
                            "  where  ProductTable.productNo = '" + pNum + "' " +
                            "  and    StockTable.productNo   = '" + pNum + "'"
            );
            if (rs.next()) {
                dt.setProductNum(pNum);
                dt.setDescription(rs.getString("description"));
                dt.setPrice(rs.getDouble("price"));
                dt.setQuantity(rs.getInt("stockLevel"));
            }
            rs.close();
            return dt;
        } catch (SQLException e) {
            throw new StockException("SQL getDetails: " + e.getMessage());
        }
    }

    public synchronized byte[] getImage(String pNum) throws StockException { // Change return type to byte[]
        String filename = "default.png";
        try {
            ResultSet rs = getStatementObject().executeQuery(
                    "select picture from ProductTable " +
                            "  where  ProductTable.productNo = '" + pNum + "'"
            );

            boolean res = rs.next();
            if (res)
                filename = rs.getString("picture");
            rs.close();
        } catch (SQLException e) {
            DEBUG.error("getImage()\n%s\n", e.getMessage());
            throw new StockException("SQL getImage: " + e.getMessage());
        }

        DEBUG.trace("DB StockR: getImage -> %s", filename);

        try (FileInputStream fis = new FileInputStream(filename);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new StockException("Error reading image file: " + e.getMessage());
        }
    }
}
