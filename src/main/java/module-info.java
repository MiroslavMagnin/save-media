module com.vadmax.savemedia {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.vadmax.savemedia to javafx.fxml;
    exports com.vadmax.savemedia;
}