module com.beginsecure.tunisairaeroplan {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;
    requires jdk.jdi;
    requires mysql.connector.java;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;
    requires javafx.media;

    opens com.beginsecure.tunisairaeroplan to javafx.fxml;
    opens com.beginsecure.tunisairaeroplan.Controller to javafx.fxml;
    opens com.beginsecure.tunisairaeroplan.utilites to javafx.graphics; // Ajout de cette ligne

    exports com.beginsecure.tunisairaeroplan;
    exports com.beginsecure.tunisairaeroplan.Model;
    exports com.beginsecure.tunisairaeroplan.Controller;
    exports com.beginsecure.tunisairaeroplan.utilites;
}
