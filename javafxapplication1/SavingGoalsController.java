/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxapplication1;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author Beleno, Itchon, Papas
 */
public class SavingGoalsController implements Initializable {

    @FXML
    private TextField targetCostTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label current_savings_label;
    @FXML
    private Label target_savings_label;
    @FXML
    private LineChart<String, Number> progressChart;
    @FXML
    private ComboBox<String> goals_cbox;
    @FXML
    private Button saveGoalButton;
    @FXML
    private TextField progressCostTextField;
    @FXML
    private TextField goalTextField;
    @FXML
    private Button saveProgressBtn;
    @FXML
    private DatePicker endPickDate;
    
    private Map<Integer, SavingGoals> goalIdMap;
    private ObservableList<SavingGoals> savingGoals;
    private Map<SavingGoals, ProgressBar> goalProgressBarMap = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UnaryOperator<TextFormatter.Change> numericFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[0-9]*$")) { // Regex to allow only numbers
                return change;
            }
            return null;
        };
        targetCostTextField.setTextFormatter(new TextFormatter<>(numericFilter));
        targetCostTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });
        progressCostTextField.setTextFormatter(new TextFormatter<>(numericFilter));
        progressCostTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });
        
        loadSavingGoalsData();
        
        goalIdMap = new HashMap<>();
        for (SavingGoals goal : savingGoals) {
            goalIdMap.put(goal.getGoalId(), goal);
        }

        // Populate ComboBox with the loaded data
        loadUserGoals(1);

        saveGoalButton.setOnAction(event -> {
                    // Call the method to handle the button functionality
                        saveSavingGoal(1);
        });
        
        // Populate ComboBox with the loaded data
        goals_cbox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Split the selected item string to get the goal ID
                String[] parts = newValue.split(":");
                if (parts.length == 2) {
                    // Get the selected goal ID from the split string
                    int selectedGoalId = Integer.parseInt(parts[0].trim());

                    // Retrieve the selected goal from the map
                    SavingGoals selectedGoal = goalIdMap.get(selectedGoalId);

                    //Update the progress bar
                    updateGoalProgressForUser(selectedGoal.getUserId(), selectedGoalId);
                    
                    loadProgressDataAndUpdateChart(selectedGoal.getUserId(), selectedGoalId);
                    //update target cost 
                    
                    saveProgressBtn.setOnAction(event -> {
                        saveUserProgressGoal(selectedGoal.getUserId(), selectedGoalId, Double.parseDouble(progressCostTextField.getText()));
                    });
                }
            }
        });
    }
    
    private void saveSavingGoal(int user_id) {
    try (Connection connection = DBConnect.reopenConnection(DBConnect.getConnection())) {
        String sql = "INSERT INTO savings_goals (user_id, goal_name, target_amount, deadline_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Get data from the form
             // Replace with the actual user ID
            String goalName = goalTextField.getText();
            double targetCost = Double.parseDouble(targetCostTextField.getText());
            LocalDate endDate = endPickDate.getValue();

            // Set values to the prepared statement
            pstmt.setInt(1, user_id);
            pstmt.setString(2, goalName);
            pstmt.setDouble(3, targetCost);
            pstmt.setDate(4, Date.valueOf(endDate));

            // Execute the INSERT query
            int rowsAffected = pstmt.executeUpdate();

            // Check if the insertion was successful
            if (rowsAffected > 0) {
                System.out.println("Saving goal saved successfully");
                loadUserGoals(1);
                loadSavingGoalsData();
            } else {
                System.out.println("Error saving saving goal");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error saving saving goal");
    }
}
    
    private void saveUserProgressGoal(int userId, int goalId, double amount) {
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())) {
            conn.setAutoCommit(false); // Start a transaction

            // Fetch the current amount from savings_goals
            String selectQuery = "SELECT current_amount, target_amount FROM savings_goals WHERE user_id = ? AND goal_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, userId);
                selectStmt.setInt(2, goalId);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        double currentAmount = rs.getDouble("current_amount");
                        double targetAmount = rs.getDouble("target_amount");
                        
                        
                        // Insert a new record in goals_progress
                        String insertQuery = "INSERT INTO goals_progress (user_id, goal_id, progress_amount) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, goalId);
                            insertStmt.setDouble(3, amount);
                            insertStmt.executeUpdate();
                        }
                        
                        // Update the current amount in savings_goals
                        String updateQuery = "UPDATE savings_goals SET current_amount = ?, is_achieved = ? WHERE user_id = ? AND goal_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setDouble(1, currentAmount + amount);
                            if (currentAmount>=targetAmount){
                                updateStmt.setInt(2, 1);
                            } else {
                                updateStmt.setNull(2, Types.INTEGER);
                            }
                            updateStmt.setInt(3, userId);
                            updateStmt.setInt(4, goalId);
                            updateStmt.executeUpdate();
                            int rowsAffected = updateStmt.executeUpdate();
                            if (rowsAffected>=1){
                                loadSavingGoalsData();
                                updateGoalProgressForUser(userId, goalId);
                                loadProgressDataAndUpdateChart(userId, goalId);
                            } else {
                                System.out.println("CANT ACTION");
                            }
                            
                        }                        
                    }
                }
            }

            conn.commit(); // Commit the transaction
        
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
    }
   
    
    
        
    private void loadUserGoals(int userId) {
        ObservableList<String> userGoals = FXCollections.observableArrayList();

        // Execute your SQL query to get the goals for the current user
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection());
             PreparedStatement stmt = conn.prepareStatement("SELECT goal_id, goal_name FROM savings_goals WHERE user_id = ?")) {
                
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                goals_cbox.getItems().clear();
                while (rs.next()) {
                    // Assuming "goal_id" and "goal_name" are columns in your table
                    int goalId = rs.getInt("goal_id");
                    String goalName = rs.getString("goal_name");

                    // Add the goal name to the ComboBox, with the goal ID as the value
                    goals_cbox.getItems().add(goalId + ": " + goalName);
                    
                    // Update the goalIdMap with the goal ID and corresponding SavingGoals object
                    goalIdMap.put(goalId, new SavingGoals(goalId, userId, goalName, 0, 0, false, null, null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }
    
    public void loadProgressDataAndUpdateChart(int userId, int goalId) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Progress Over Time");
        progressChart.getData().clear();
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection());
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM goals_progress WHERE user_id = ? and goal_id = ? ORDER BY progress_date")) {

            stmt.setInt(1, userId);
            stmt.setInt(2, goalId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    // No progress records for the selected goal, add a data point with zero progress
                    series.getData().add(new XYChart.Data<>("No Progress", 0));
                } else {
                    do {
                        // Assuming progress_date is of type Date or similar
                        Date progressDate = rs.getDate("progress_date");
                        double progressAmount = rs.getDouble("progress_amount");

                        // Format the date as a string for the X-axis
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(progressDate);

                        // Add data to the series
                        series.getData().add(new XYChart.Data<>(formattedDate, progressAmount));
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        // Log the series data to the console
        System.out.println("Series Data: " + series.getData());

        // Update the chart with the series
        progressChart.getData().add(series);
    }

    
    private void loadSavingGoalsData() {
        savingGoals = FXCollections.observableArrayList();

        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM savings_goals")) {

            while (rs.next()) {
                SavingGoals goal = new SavingGoals(
                        rs.getInt("goal_id"),
                        rs.getInt("user_id"),
                        rs.getString("goal_name"),
                        rs.getDouble("target_amount"),
                        rs.getDouble("current_amount"),
                        rs.getBoolean("is_achieved"),
                        rs.getDate("deadline_date"),
                        rs.getDate("creation_date")
                );

                savingGoals.add(goal);
                goalProgressBarMap.put(goal, progressBar);
            }
               
            System.out.println(savingGoals);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading saving goals data.");
        }
    }

        
    private void changeTargetSavingCost(int userId, int goalId){
        String targetSavingCost = targetCostTextField.getText();
        try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection())){
            PreparedStatement stmt = conn.prepareStatement("UPDATE savings_goals SET target_amount = ? WHERE user_id = ? AND goal_id = ?");

            // Assuming targetSavingCost is a double or can be parsed as a double
            double targetSavingCostValue = Double.parseDouble(targetSavingCost);

            // Set the parameters for the prepared statement
            stmt.setDouble(1, targetSavingCostValue); // First parameter (target_amount)
            stmt.setInt(2, userId); // Second parameter (user_id)
            stmt.setInt(3, goalId); // Second parameter (user_id)

            // Execute the update
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected>=1){
                loadSavingGoalsData();
                updateGoalProgressForUser(userId, goalId);
            } else {
                System.out.println("CANT UPDATE ");
            }
        } catch(Exception e) {
            
        }
    }
     
    @FXML
    private void updateGoalProgressForUser(int userId, int goalId) {
        for (SavingGoals goal : savingGoals) {
            if (goal.getUserId() == userId && goal.getGoalId() == goalId) {
                double progress = goal.calculateProgress();
                if (progressBar != null) {
                    progressBar.setProgress(progress);
                    current_savings_label.setText("$"+Double.toString(goal.getCurrentAmount()));
                    current_savings_label.setWrapText(true); // Enable text wrapping
                    current_savings_label.setMaxWidth(Double.MAX_VALUE);
                    target_savings_label.setText("$"+Double.toString(goal.getTargetAmount()));
                    target_savings_label.setWrapText(true);
                    target_savings_label.setMaxWidth(Double.MAX_VALUE);
                }
            }
        }
    }
}
