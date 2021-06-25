module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;

    opens org.example to javafx.fxml;
    exports org.example;
}