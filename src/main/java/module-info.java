module com.vadmax.savemedia {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.jdom2;
    requires java.desktop;
    requires org.apache.commons.io;

    opens com.vadmax.savemedia to javafx.fxml;
    opens com.vadmax.savemedia.downloadsettings to javafx.base;
    opens com.vadmax.savemedia.exception to javafx.fxml;
    exports com.vadmax.savemedia;
    exports com.vadmax.savemedia.exception;
    exports com.vadmax.savemedia.gui;
    opens com.vadmax.savemedia.gui to javafx.fxml;
}