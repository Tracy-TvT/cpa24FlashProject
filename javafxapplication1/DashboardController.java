package javafxapplication1;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javax.sql.DataSource;


public class DashboardController implements Initializable {
 @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         
    }

    private int userId;
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId(){
        return userId;
    }
    
}
