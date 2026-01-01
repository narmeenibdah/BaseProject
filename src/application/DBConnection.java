package application;

import java.sql.Connection;
import java.sql.DriverManager;

import javafx.scene.control.Alert;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url  = "jdbc:mysql://localhost:3306/birzeitPharmacy"; 
            String user = "root";
            String pass = "admin"; 

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Cannot connect to the database");
            alert.setContentText(
                "Please check:\n" +
                "- MySQL service is running\n" +
                "- Database name is correct\n" +
                "- Username & password are correct"
            );
            alert.showAndWait();
            return null;
        }
    }
}