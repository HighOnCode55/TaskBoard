module com.github.highoncode55.taskboard.taskboard {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires java.sql;
    requires com.github.highoncode55.taskboard.taskboard;

    opens com.github.highoncode55.taskboard to javafx.fxml;
    exports com.github.highoncode55.taskboard;
    exports com.github.highoncode55.taskboard.controller;
    opens com.github.highoncode55.taskboard.controller to javafx.fxml;
}