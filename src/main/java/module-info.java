module CI553.ministore {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.rmi;

    // Derby modules in correct order
    requires org.apache.derby.commons;
    requires org.apache.derby.engine;
    requires org.apache.derby.client;
    requires org.apache.derby.tools;

    // Opens for FXML
    opens ci553.ministore to javafx.fxml;
    opens ci553.ministore.clients.start to javafx.fxml;
    opens ci553.ministore.clients.cashierjavafx to javafx.fxml;
    opens ci553.ministore.clients.customerjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.dashboard to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.packing to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.stockmanagement to javafx.fxml;
    opens ci553.ministore.dbAccess to org.apache.derby.commons;

    // Add missing exports
    exports ci553.ministore;
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
