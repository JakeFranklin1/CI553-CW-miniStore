package ci553.ministore.clients.start;

import ci553.ministore.clients.Setup;
import ci553.ministore.dbAccess.DBAccess;
import ci553.ministore.dbAccess.DBAccessFactory;
import java.sql.*;

@SuppressWarnings("unused")
public class AutoSetup {
    private static boolean runAuto = false;  // Flag to control automatic setup

    public static void setRunAuto(boolean value) {
        runAuto = value;
    }

    public static boolean isRunAuto() {
        return runAuto;
    }

    public static void initializeIfNeeded() {
        if (runAuto) {
            System.out.println("Running automatic setup...");
            Setup.main(new String[0]);
        } else {
            System.out.println("Automatic setup is disabled");
        }
    }
}
