package ci553.ministore.dbAccess;

/**
 * Apache Derby database access.
 * Provides methods to load the database driver and get the database URL.
 * This class extends DBAccess to provide specific implementation for Apache Derby.
 *
 * @version 2.0
 */
class DerbyAccess extends DBAccess {
    private static final String URLdb = "jdbc:derby:catshop.db";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    /**
     * Load the Apache Derby database driver.
     * Uses reflection to load the driver class.
     *
     * @throws Exception If there is an error loading the driver
     */
    public void loadDriver() throws Exception {
        // Load the driver class using reflection
        Class.forName(DRIVER).getDeclaredConstructor().newInstance();
    }

    /**
     * Return the URL to access the database.
     *
     * @return The URL to the database
     */
    public String urlOfDatabase() {
        return URLdb;
    }
}
