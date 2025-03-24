module org.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires morphia.core;
    requires org.mongodb.bson;
    requires java.management;
    requires org.jsoup;

    opens org.example.projekt to javafx.fxml;
    exports org.example.projekt;

    exports backend;
    exports backend.klasy;
    exports backend.wyjatki;
    exports backend.mongo;
    exports backend.status;
}