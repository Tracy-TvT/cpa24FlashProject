package javafxapplication1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyEvent;

public class ExpensesIncomeController implements Initializable  {

    @FXML
    private BarChart<String, Number> recurringChart;

    @FXML
    private ScatterChart incomeScatterChart;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button saveTransactionButton;

     @FXML
    private ComboBox<String> categoryComboBox1;

    @FXML
    private TextField amountTextField1;

    @FXML
    private TextArea descriptionTextArea1;

    @FXML
    private DatePicker startPickDate;

    @FXML
    private DatePicker endPickDate;
    @FXML
    private ComboBox patternComboBox;

    @FXML
    private Button saveRecurringEnButton;
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        typeComboBox.getItems().addAll("Income", "Expense");
        patternComboBox.getItems().addAll("Daily", "Weekly", "Mothly", "Yearly");
        initializeCharts(1); // initialize then passw 
        fetchCategoriesItems();
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[0-9]*$")) { // Regex to allow only numbers
                return change;
            }
            return null;
        };
        saveTransactionButton.setOnAction(event -> saveTransaction(1));
        saveRecurringEnButton.setOnAction(event -> handleSaveRecurringExpense(1));
        amountTextField.setTextFormatter(new TextFormatter<>(numericFilter));
        amountTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });
        amountTextField1.setTextFormatter(new TextFormatter<>(numericFilter));
        amountTextField1.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });
        
    }

    @FXML
    private void handleSaveRecurringExpense(int user_id) {
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "INSERT INTO recurring_expenses (category_id, amount, description, start_date, end_date, user_id, recurrence_pattern) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String selectedCategoryName = categoryComboBox1.getValue();
                int categoryId = Integer.parseInt(selectedCategoryName.split(":")[0].trim());

                pstmt.setInt(1, categoryId);
                pstmt.setDouble(2, Double.parseDouble(amountTextField1.getText()));
                pstmt.setString(3, descriptionTextArea1.getText());
                pstmt.setTimestamp(4, Timestamp.valueOf(startPickDate.getValue().atStartOfDay()));
                pstmt.setTimestamp(5, Timestamp.valueOf(endPickDate.getValue().atStartOfDay()));
                pstmt.setInt(6, user_id);
                pstmt.setString(7, patternComboBox.getValue().toString().toUpperCase());
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    initializeCharts(1);
                } else {
            
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            
        }
    }

    
    private void initializeCharts(int user_id) {
        loadBarFromDatabase(user_id);
        loadScatterFromDatabase(user_id);
    }
    
    private void fetchCategoriesItems(){
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())){
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT category_id, category_name FROM categories")){
              ResultSet rs = pstmt.executeQuery();
              ObservableList<String> categoriesList = FXCollections.observableArrayList();
              while(rs.next()){
                  String categoryName = rs.getString("category_name");
                  int categoryId = rs.getInt("category_id");
                  categoriesList.add(categoryId +": "+ categoryName);
              }
              categoryComboBox.setItems(categoriesList);
              categoryComboBox1.setItems(categoriesList);
            } 
            
            
        } catch (Exception e){
            
        }
    }

    private void saveTransaction(int user_id) {
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "INSERT INTO transactions (category_id, amount, description, transaction_date, transaction_type, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String selectedCategoryName = categoryComboBox.getValue();
                String selectedType = typeComboBox.getValue();
                String transactionType = null;
                int categoryId = 0;
                if (selectedCategoryName!=null){
                    categoryId = Integer.parseInt(selectedCategoryName.split(":")[0].trim());
                }
                if (selectedType!=null){
                    transactionType=selectedType.toLowerCase();
                }
                pstmt.setInt(1, categoryId); // Set category_id
                pstmt.setDouble(2, Double.parseDouble(amountTextField.getText())); // Set amount
                pstmt.setString(3, descriptionTextArea.getText()); // Set description
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); // Set transaction_date
                pstmt.setString(5, transactionType); // Set transaction_type
                pstmt.setInt(6, user_id);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Update charts after saving the transaction
                    initializeCharts(user_id);
                } else {
                 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }

    private void loadScatterFromDatabase(int user_id) {
    // Assuming you have a method to get a database connection (e.g., DBConnect.getConnection())
    try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
        String sql = "SELECT description, amount FROM transactions WHERE user_id = ? AND transaction_type = 'income'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                while (rs.next()) {
                    String description = rs.getString("description");
                    double amount = rs.getDouble("amount");

                    // Add data points to the series
                    series.getData().add(new XYChart.Data<>(description, amount));
                }

                // Clear existing data and add the new series to the Scatter Chart
                incomeScatterChart.getData().clear();
                incomeScatterChart.getData().add(series);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error loading data from the database.");
    }
}
    
    private void loadBarFromDatabase(int user_id) {
        // Assuming you have a method to get a database connection (e.g., DBConnect.getConnection())
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "SELECT category_name, SUM(amount) AS total_amount \n" +
                        "FROM recurring_expenses \n" +
                        "JOIN categories ON recurring_expenses.category_id = categories.category_id \n" +
                        "WHERE user_id = ? \n" +
                        "GROUP BY recurring_expenses.category_id;";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, user_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    recurringChart.getData().clear();
                    while (rs.next()) {
                        String categoryName = rs.getString("category_name");
                        double totalAmount = rs.getDouble("total_amount");

                        // Populate the chart
                        XYChart.Series<String, Number> series = new XYChart.Series<>();
                        series.getData().add(new XYChart.Data<>(categoryName, totalAmount));
                        recurringChart.getData().add(series);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from the database.");
        }
    }
}
