/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication1;

import java.sql.Timestamp;
import java.util.Date;
import javafx.beans.property.*;

/**
 *
 * @author Beleno, Itchon, Papas
 */
 public class Transaction {
    private final SimpleIntegerProperty transactionId;
    private final SimpleIntegerProperty userId;
    private final SimpleIntegerProperty categoryId;
    private final SimpleDoubleProperty amount;
    private final SimpleStringProperty description;
    private final SimpleObjectProperty<java.sql.Timestamp> transactionDate;
    private final SimpleStringProperty transactionType;
    private SimpleStringProperty categoryName;

    public Transaction(int transactionId, int userId, int categoryId, double amount, String description, java.sql.Timestamp transactionDate, String transactionType, String categoryName) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.userId = new SimpleIntegerProperty(userId);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.amount = new SimpleDoubleProperty(amount);
        this.description = new SimpleStringProperty(description);
        this.transactionDate = new SimpleObjectProperty<>(transactionDate);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.categoryName = new SimpleStringProperty(categoryName);
    }

    
    
    // Getters and setters for each property
    public void setCategoryName(String name){
        this.categoryName.set(name);
    }
    
    public String getCategoryName() {
        return categoryName.get();
    }
    
    
    public int getTransactionId() {
        return transactionId.get();
    }

    public int getUserId(){
        return userId.get();
    }
    
    public int getCategoryId(){
        return categoryId.get();
    }
    
    public double getAmount(){
        return amount.get();
    }
    
    public void setAmount(double amount){
        this.amount.set(amount);
    }
        
    public String getDescription(){
        return description.get();
    }
    
    public void setDescription(String desc){
        this.description.set(desc);
    }
    
    public Date getTransactionDate(){
        return transactionDate.get();
    }
    
    public String getTransactionType(){
        return transactionType.get();
    }
}



