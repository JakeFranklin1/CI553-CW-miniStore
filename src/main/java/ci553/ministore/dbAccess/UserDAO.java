package dbAccess;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import middle.StockException;
import util.PasswordUtil;

public class UserDAO {
    private Connection theCon = null;

    public UserDAO() throws StockException {
        try {
            DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
            dbDriver.loadDriver();

            theCon = DriverManager.getConnection(
                    dbDriver.urlOfDatabase(),
                    dbDriver.username(),
                    dbDriver.password());

            theCon.setAutoCommit(true);
        } catch (SQLException e) {
            throw new StockException("SQL problem:" + e.getMessage());
        } catch (Exception e) {
            throw new StockException("Can not load database driver.");
        }
    }

    public boolean validateUser(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String sql = "SELECT password, salt FROM Users WHERE username = ?";
        try (PreparedStatement stmt = theCon.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String salt = rs.getString("salt");
                    return PasswordUtil.validatePassword(password, salt, storedHash);
                }
            }
        }
        return false;
    }
}
