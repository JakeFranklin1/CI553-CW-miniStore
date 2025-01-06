/**
 * Module definition for the CI553 MiniStore application.
 * This module requires various JavaFX modules, Java SQL, and Java RMI.
 * It also requires Apache Derby modules for database operations.
 * The module opens specific packages for JavaFX FXML and exports packages for use by other modules.
 */
module ci553.ministore {
    // Requires JavaFX modules for UI components and FXML
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    // Requires Java SQL for database connectivity
    requires java.sql;

    // Requires Java RMI for remote method invocation
    requires java.rmi;

    // Requires Apache Derby modules in correct order for database operations
    requires org.apache.derby.commons;
    requires org.apache.derby.engine;
    requires org.apache.derby.client;
    requires org.apache.derby.tools;

    // Opens packages for JavaFX FXML to allow reflection-based access
    opens ci553.ministore.clients.start to javafx.fxml;
    opens ci553.ministore.clients.cashierjavafx to javafx.fxml;
    opens ci553.ministore.clients.customerjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.dashboard to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.packing to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.stockmanagement to javafx.fxml;
    opens ci553.ministore.dbAccess to org.apache.derby.commons;

    // Exports packages for use by other modules
    exports ci553.ministore.catalogue;
    exports ci553.ministore.clients;
    exports ci553.ministore.clients.start;
    exports ci553.ministore.clients.customerjavafx;
    exports ci553.ministore.clients.cashierjavafx;
    exports ci553.ministore.clients.staffjavafx;
    exports ci553.ministore.clients.staffjavafx.dashboard;
    exports ci553.ministore.clients.staffjavafx.packing;
    exports ci553.ministore.clients.staffjavafx.stockmanagement;
    exports ci553.ministore.dbAccess;
    exports ci553.ministore.middle;
    exports ci553.ministore.remote;
}
