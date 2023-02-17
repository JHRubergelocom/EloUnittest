module com.example.elounittest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires EloixClient;
    requires java.desktop;
    requires com.google.gson;
    requires java.rmi;

    opens com.example.elounittest to javafx.fxml;
    exports com.example.elounittest;
    exports elounittest;
    opens elounittest to javafx.fxml;
}