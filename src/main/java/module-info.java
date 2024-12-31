module CI553.ministore {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.rmi;

    opens ci553.ministore to javafx.fxml;
    opens ci553.ministore.clients.start to javafx.fxml;
    opens ci553.ministore.clients.cashierjavafx to javafx.fxml;
    opens ci553.ministore.clients.customerjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.dashboard to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.packing to javafx.fxml;
    opens ci553.ministore.clients.staffjavafx.stockmanagement to javafx.fxml;

    exports ci553.ministore;
    exports ci553.ministore.catalogue;
    exports ci553.ministore.clients;
    exports ci553.ministore.dbAccess;
    exports ci553.ministore.middle;
    exports ci553.ministore.remote;
}
