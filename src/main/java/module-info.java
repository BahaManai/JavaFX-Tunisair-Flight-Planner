module com.beginsecure.tunisairaeroplan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.beginsecure.tunisairaeroplan to javafx.fxml;
    exports com.beginsecure.tunisairaeroplan;
}