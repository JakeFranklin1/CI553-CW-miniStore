package ci553.ministore.clients;

import ci553.ministore.dbAccess.DBAccess;
import ci553.ministore.dbAccess.DBAccessFactory;
// import ci553.ministore.middle.StockException;

import java.sql.*;
import java.util.ArrayList;

/**
 * Repopulate the database with test data
 *
 * @version 3.0 Derby
 */

public class Setup {
    private static String[] sqlStatements = {

            // Existing SQL statements for ProductTable and StockTable

            "drop table ProductTable",
            "create table ProductTable (" +
                    "productNo      Char(4)," +
                    "description    Varchar(40)," +
                    "picture        Varchar(80)," +
                    "price          Float)",

            "insert into ProductTable values " +
                    "('0001', '40 inch LED HD TV', 'ci553/ministore/images/pic0001.png', 269.00)",
            "insert into ProductTable values " +
                    "('0002', 'DAB Radio',         'ci553/ministore/images/pic0002.png', 29.99)",
            "insert into ProductTable values " +
                    "('0003', 'Toaster',           'ci553/ministore/images/pic0003.png', 19.99)",
            "insert into ProductTable values " +
                    "('0004', 'Watch',             'ci553/ministore/images/pic0004.png', 29.99)",
            "insert into ProductTable values " +
                    "('0005', 'Digital Camera',    'ci553/ministore/images/pic0005.png', 89.99)",
            "insert into ProductTable values " +
                    "('0006', 'MP3 player',        'ci553/ministore/images/pic0006.png', 7.99)",
            "insert into ProductTable values " +
                    "('0007', '32Gb USB2 drive',   'ci553/ministore/images/pic0007.png', 6.99)",

            "drop table StockTable",
            "create table StockTable (" +
                    "productNo      Char(4)," +
                    "stockLevel     Integer)",

            "insert into StockTable values ( '0001',  90 )",
            "insert into StockTable values ( '0002',  20 )",
            "insert into StockTable values ( '0003',  33 )",
            "insert into StockTable values ( '0004',  10 )",
            "insert into StockTable values ( '0005',  17 )",
            "insert into StockTable values ( '0006',  15 )",
            "insert into StockTable values ( '0007',  01 )",

            "select * from StockTable, ProductTable " +
                    " where StockTable.productNo = ProductTable.productNo",

            // New SQL statements for Users table

            "drop table Users",
            "create table Users (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "salt VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

            // Insert hashed and salted passwords directly
            "insert into Users (username, password, email, salt) values ('user1', 'O4y5xpX/efjLeVyqQBW9Yx77UKXLw//0TRNZT+9RqiD7/hJdOQRKGraG9IL85dWg/ZAvQdv0KT8Lf8JJ2uNY9Q==', 'user1@example.com', 'n0r4u775zgtj9dZNwj/W/Q==')",
            "insert into Users (username, password, email, salt) values ('user2', 'Qp9MJFQXb3fFZHmHjBWocZzTQCJL5ZTCtNJE3dE/wryXPlLV1hfwHeNx0n9KlUFeiixgAvCJoHlswVaIR9YvqA==', 'user2@example.com', 'FA6bADYwiG5XDvQq0M4ouQ==')"
    };

    public static void main(String[] args) {
        Connection theCon = null; // Connection to database
        DBAccess dbDriver = null;
        DBAccessFactory.setAction("Create");
        System.out.println("Setup CatShop database of stock items");
        try {
            dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();
            theCon = DriverManager.getConnection(dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password());
        } catch (SQLException e) {
            System.err.println("Problem with connection to " +
                    dbDriver.urlOfDatabase());
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState:     " + e.getSQLState());
            System.out.println("VendorError:  " + e.getErrorCode());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("Can not load JDBC/ODBC driver.");
            System.exit(-1);
        }

        Statement stmt = null;
        try {
            stmt = theCon.createStatement();
        } catch (Exception e) {
            System.err.println("problems creating statement object");
        }

        // execute SQL commands to create table, insert data
        for (String sqlStatement : sqlStatements) {
            try {
                System.out.println(sqlStatement);
                switch (sqlStatement.charAt(0)) {
                    case '/':
                        System.out.println("------------------------------");
                        break;
                    case 's':
                    case 'f':
                        query(stmt, dbDriver.urlOfDatabase(), sqlStatement);
                        break;
                    case '*':
                        if (sqlStatement.length() >= 2)
                            switch (sqlStatement.charAt(1)) {
                                case 'c':
                                    theCon.commit();
                                    break;
                                case 'r':
                                    theCon.rollback();
                                    break;
                                case '+':
                                    theCon.setAutoCommit(true);
                                    break;
                                case '-':
                                    theCon.setAutoCommit(false);
                                    break;
                            }
                        break;
                    default:
                        stmt.execute(sqlStatement);
                }
                // System.out.println();
            } catch (Exception e) {
                System.out.println("problems with SQL sent to " +
                        dbDriver.urlOfDatabase() +
                        "\n" + sqlStatement + "\n" + e.getMessage());
            }
        }

        try {
            theCon.close();
        } catch (Exception e) {
            System.err.println("problems with close " +
                    ": " + e.getMessage());
        }

    }

    private static void query(Statement stmt, String url, String stm) {
        try {
            ResultSet res = stmt.executeQuery(stm);

            ArrayList<String> names = new ArrayList<>(10);

            ResultSetMetaData md = res.getMetaData();
            int cols = md.getColumnCount();

            for (int j = 1; j <= cols; j++) {
                String name = md.getColumnName(j);
                System.out.printf("%-14.14s ", name);
                names.add(name);
            }
            System.out.println();

            for (int j = 1; j <= cols; j++) {
                System.out.printf("%-14.14s ", md.getColumnTypeName(j));
            }
            System.out.println();

            while (res.next()) {
                for (int j = 0; j < cols; j++) {
                    String name = names.get(j);
                    System.out.printf("%-14.14s ", res.getString(name));
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("problems with SQL sent to " + url +
                    "\n" + e.getMessage());
        }
    }

    // private static String m(int len, String s) {
    //     if (s.length() >= len) {
    //         return s.substring(0, len - 1) + " ";
    //     } else {
    //         StringBuilder res = new StringBuilder(len);
    //         res.append(s);
    //         for (int i = s.length(); i < len; i++)
    //             res.append(' ');
    //         return res.toString();
    //     }
    // }

}
