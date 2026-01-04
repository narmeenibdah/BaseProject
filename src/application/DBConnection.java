package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url  = "jdbc:mysql://localhost:3306/birzeitPharmacy"; 
            String user = "root";
            String pass = "1230778HibaAhmad"; 

            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}