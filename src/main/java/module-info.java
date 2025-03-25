module com.example.zavrsniprojektvojvodic {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires org.apache.commons.codec;


    opens com.example.zavrsniprojektvojvodic to javafx.fxml;
    exports com.example.zavrsniprojektvojvodic;
}