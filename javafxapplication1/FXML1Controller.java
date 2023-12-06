package javafxapplication1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Beleno, Itchon, Papas
 */
public class FXML1Controller implements Initializable {

    @FXML
    private TextField username_txt;

    @FXML
    private PasswordField password_txt;

    @FXML
    private Button login_btn;

    
    @FXML
    private void handleLoginButton(ActionEvent event) throws IOException {
        String username = username_txt.getText();
        String password = password_txt.getText();

        // Add your login logic here
        int userId = authenticateUser(username, password);

        if (userId != -1) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxapplication1/dashboard.fxml"));
            Parent trunk = loader.load();

            // Create a new stage
            Stage stage = new Stage();
            stage.setTitle("Dashboard");

            // Set the new scene
            Scene scene = new Scene(trunk);
            stage.setScene(scene);

            // Show the new stage
            stage.show();

            // Optionally, close the current stage
            ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
        } else {
            System.out.println("Invalid login credentials");
            // Add logic to handle invalid login credentials (e.g., show an error message)
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private int authenticateUser(String username, String password) {
        try (Connection connection = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Return the user_id if authentication is successful
                        return rs.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 if authentication fails
        return -1;
    }
}
