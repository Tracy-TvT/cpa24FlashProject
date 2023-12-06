/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxapplication1;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;
/**
 * FXML Controller class
 *
 * @author Beleno, Itchon, Papas
 */
public class TransactionsController implements Initializable {

    @FXML
    private TableView<Transaction> transactionsTableView;
    @FXML
    private TableColumn<Transaction, Integer> columnTransactionId;
    @FXML
    private TableColumn<Transaction, Integer> columnUserId;
    @FXML
    private TableColumn<Transaction, Integer> columnCategoryName;
    @FXML
    private TableColumn<Transaction, Double> columnAmount;
    @FXML
    private TableColumn<Transaction, String> columnDescription;
    @FXML
    private TableColumn<Transaction, java.sql.Timestamp> columnTransactionDate;
    @FXML
    private TableColumn<Transaction, String> columnTransactionType;
    @FXML
    private TextField filterTextField;

    
    private ObservableList<Transaction> allTransactions;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        columnUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnTransactionDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        columnTransactionType.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        allTransactions = FXCollections.observableArrayList();
        loadTransactionData(1);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions(newValue);
        });
    }    
    private void loadTransactionData(int user_id) {
    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    try (Connection conn = DBConnect.reopenConnection(DBConnect.getConnection());
         PreparedStatement stmt = conn.prepareStatement("SELECT t.*, c.category_name " +
                 "FROM transactions t " +
                 "JOIN categories c ON t.category_id = c.category_id " +
                 "WHERE t.user_id = ?")) {
        stmt.setInt(1, user_id);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("user_id"),
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("transaction_date"),
                        rs.getString("transaction_type"),
                        rs.getString("category_name")
                );
                transactions.add(transaction);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error loading transaction data.");
    }

    transactionsTableView.setItems(transactions);
}


    private void filterTransactions(String keyword) {
        if (keyword.isEmpty()) {
            loadTransactionData(1);
        } else {
            ObservableList<Transaction> filteredList = FXCollections.observableArrayList();

            for (Transaction transaction : transactionsTableView.getItems()) {
                if (transaction.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(transaction);
                }
            }

            transactionsTableView.setItems(filteredList);
        }
    }
}
