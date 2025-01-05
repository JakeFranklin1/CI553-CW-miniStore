package ci553.ministore.clients.start;

import ci553.ministore.clients.Setup;
import ci553.ministore.dbAccess.DBAccess;
import ci553.ministore.dbAccess.DBAccessFactory;
import java.sql.*;

/**
 * Utility class for automatic setup of the MiniStore application.
 * Controls whether the setup process should run automatically and initializes
 * the setup if needed.
 */
@SuppressWarnings("unused")
public class AutoSetup {
    private static boolean runAuto = false; // Flag to control automatic setup

    /**
     * Sets the flag to control automatic setup.
     * 
     * @param value True to enable automatic setup, false to disable.
     */
    public static void setRunAuto(boolean value) {
        runAuto = value;
    }

    /**
     * Checks if automatic setup is enabled.
     * 
     * @return True if automatic setup is enabled, false otherwise.
     */
    public static boolean isRunAuto() {
        return runAuto;
    }

    /**
     * Initializes the setup process if automatic setup is enabled.
     * If the runAuto flag is true, it runs the Setup.main method to perform the
     * setup.
     */
    public static void initializeIfNeeded() {
        if (runAuto) {
            System.out.println("Running automatic setup...");
            Setup.main(new String[0]); // Run the setup process
        } else {
            System.out.println("Automatic setup is disabled");
        }
    }
}
