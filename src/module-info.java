module LiveMusicVenueMatchmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Required for SQLite database connection
    requires org.controlsfx.controls; // If using ControlsFX UI components

    // Open packages to JavaFX for FXML controllers
    opens controller to javafx.fxml;
    opens app to javafx.fxml;
    opens service to javafx.fxml;

    // Export packages to be accessible by other modules
    exports controller;
    exports app;
    exports service;
    exports model;
    exports dao;
}
