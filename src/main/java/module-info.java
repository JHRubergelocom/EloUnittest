module com.example.elounittest {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.fx.countries;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.elounittest to javafx.fxml;
    exports com.example.elounittest;
}