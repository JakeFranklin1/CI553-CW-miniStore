package ci553.ministore.dbAccess;

/**
 * Implements management of a MySQL database on Linux.
 * Provides methods to load the database driver and get the database URL.
 * This class extends DBAccess to provide specific implementation for MySQL on Linux.
 *
 * @version 2.0
 */
class LinuxAccess extends DBAccess {

    /**
     * Load the MySQL database driver.
     * Uses reflection to load the driver class.
     *
     * @throws Exception If there is an error loading the driver
     */
    public void loadDriver() throws Exception {
        // Load the driver class using reflection
        Class.forName("org.gjt.mm.mysql.Driver").getDeclaredConstructor().newInstance();
    }

    /**
     * Return the URL to access the database.
     *
     * @return The URL to the database
     */
    public String urlOfDatabase() {
        return "jdbc:mysql://localhost/cshop?user=root";
    }
}
