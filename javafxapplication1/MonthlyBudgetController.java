package javafxapplication1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MonthlyBudgetController implements Initializable {

    @FXML
    private TableView<Budget> budgetTable;

    @FXML
    private TableColumn<Budget, Integer> budgetIdColumn;

    @FXML
    private TableColumn<Budget, Integer> userIdColumn;

    @FXML
    private TableColumn<Budget, Category> categoryNameColumn; // Change the type to Category

    @FXML
    private TableColumn<Budget, Double> monthlyLimitColumn;

    @FXML
    private Button deleteButton;

    // New Form Elements
    @FXML
    private TextField monthlyLimitTextField;
    @FXML
    private TextField filterBudgetTextField;

    @FXML
    private ComboBox<Category> categoryComboBox; // Change the type to Category

    @FXML
    private Button saveBudgetButton;
    // End of New Form Elements

    private ObservableList<Budget> budgets;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the table columns
        budgetIdColumn.setCellValueFactory(new PropertyValueFactory<>("budgetId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        monthlyLimitColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyLimit"));
        filterBudgetTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBudgets(newValue);
        });
        // Initialize the data in the table
        loadData(1);

        // Set the button actions
        deleteButton.setOnAction(event -> handleDeleteButton());

        // Set up the categoryComboBox
        setupCategoryComboBox();

        // Set the action for saveBudgetButton
        saveBudgetButton.setOnAction(event -> saveBudget());
    }

    private void filterBudgets(String keyword) {
        if (keyword.isEmpty()) {
            loadData(1);
        } else {
            ObservableList<Budget> filteredList = FXCollections.observableArrayList();

            for (Budget budget : budgets) {
                if (budget.getCategoryName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(budget);
                }
            }

            budgetTable.setItems(filteredList);
        }
    }

    private void loadData(int user_id) {
        budgets = FXCollections.observableArrayList();

        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "SELECT b.*, c.category_id, c.category_name FROM budgets b " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "WHERE b.user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Set the parameter value
                pstmt.setInt(1, user_id);

                // Execute the query and process the ResultSet
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        // Process the results
                        int budgetId = rs.getInt("budget_id");
                        int userId = rs.getInt("user_id");
                        int categoryId = rs.getInt("category_id");
                        double monthlyLimit = rs.getDouble("monthly_limit");

                        // Create a Budget object or process the data as needed
                        Budget budget = new Budget(budgetId, userId, categoryId, monthlyLimit);

                        Category category = new Category(categoryId, rs.getString("category_name"));
                        budget.setCategoryName(category.getCategoryName());

                        // Add the budget to your data structure (e.g., ObservableList)
                        budgets.add(budget);
                    }
                    // Set the data to the table
                    budgetTable.setItems(budgets);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading budget data.");
        }
    }

    private void handleDeleteButton() {
        Budget selectedBudget = budgetTable.getSelectionModel().getSelectedItem();
        if (selectedBudget != null) {
            try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM budgets WHERE budget_id = ?")) {
                    pstmt.setInt(1, selectedBudget.getBudgetId());
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 1) {
                        System.out.println("Delete budget with ID: " + selectedBudget.getBudgetId());
                        loadData(1);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    // New method to set up the categoryComboBox
    private void setupCategoryComboBox() {
        // Assume you have a method to get categories from the database
        ObservableList<Category> categories = getCategories();

        // Set items to the categoryComboBox
        categoryComboBox.setItems(categories);
    }

    // New method to get categories from the database
    private ObservableList<Category> getCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();

        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "SELECT * FROM categories";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int categoryId = rs.getInt("category_id");
                        String categoryName = rs.getString("category_name");
                        Category category = new Category(categoryId, categoryName);
                        categories.add(category);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading categories.");
        }

        return categories;
    }

    // New method to save a new budget
    private void saveBudget() {
        try (Connection connection = DBConnect.reopenConnection(DBConnect.getConnection())) {
            String sql = "INSERT INTO budgets (user_id, category_id, monthly_limit) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                // Get data from the form
                int userId = 1; // Replace with the actual user ID
                Category selectedCategory = categoryComboBox.getValue();
                double monthlyLimit = Double.parseDouble(monthlyLimitTextField.getText());

                // Set values to the prepared statement
                pstmt.setInt(1, userId);
                pstmt.setInt(2, selectedCategory.getCategoryId());
                pstmt.setDouble(3, monthlyLimit);

                // Execute the INSERT query
                int rowsAffected = pstmt.executeUpdate();

                // Check if the insertion was successful
                if (rowsAffected > 0) {
                    System.out.println("Budget saved successfully");
                    // Reload data to update the table
                    loadData(userId);
                } else {
                    System.out.println("Error saving budget");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving budget");
        }
    }
}
