module com.beginsecure.tunisairaeroplan {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.sql;
    requires jdk.jdi;

    opens com.beginsecure.tunisairaeroplan to javafx.fxml;
    opens com.beginsecure.tunisairaeroplan.Controller.vol to javafx.fxml;

    exports com.beginsecure.tunisairaeroplan;
}
