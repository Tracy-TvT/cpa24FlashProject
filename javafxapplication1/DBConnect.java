/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Beleno, Itchon, Papas
 */
public class DBConnect {
    private static Connection connection = null;
    
    
    public static Connection getConnection(){
        String url = "jdbc:mysql://localhost:127.0.0.1/financial_tracker"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String password = ""; 
        
        if (connection == null) {
            try {
                // You might not need to explicitly load the driver with newer JDBC versions
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("DB not found");
                
            }
        }
        return connection;
    }
    
    public static Connection reopenConnection(Connection closedConnection) {
        String url = "jdbc:mysql://localhost:127.0.0.1/financial_tracker"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String password = ""; 
        try {
            if (closedConnection != null && closedConnection.isClosed()) {
                return DriverManager.getConnection(url, user, password);
            }
            return closedConnection;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle the exception according to your application's requirements
        }
    }
}
