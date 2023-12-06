/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication1;

/**
 *
 * @author Beleno, Itchon, Papas
 */
public class Budget {
    private int budgetId;
    private int userId;
    private int categoryId;
    private double monthlyLimit;
    private String categoryName;

    public Budget(int budgetId, int userId, int categoryId, double monthlyLimit) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.monthlyLimit = monthlyLimit;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }
    
    public String getCategoryName(){
        return categoryName;
    }
    
    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }
}
