package javafxapplication1;

import java.util.Date;

public class SavingGoals {

    private int goalId;
    private int userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private boolean isAchieved;
    private Date deadlineDate;
    private Date creationDate;

    public SavingGoals(int goalId, int userId, String goalName, double targetAmount,
                       double currentAmount, boolean isAchieved, Date deadlineDate, Date creationDate) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.isAchieved = isAchieved;
        this.deadlineDate = deadlineDate;
        this.creationDate = creationDate;
    }

    // Getters and setters for all fields

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isAchieved() {
        return isAchieved;
    }

    public void setAchieved(boolean achieved) {
        isAchieved = achieved;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    // process
    public double calculateProgress() {
        if (targetAmount > 0) {
            return currentAmount / targetAmount;
        } else {
            return 0.0;
        }
    }
    
    public double getCurrentSavings() {
        return targetAmount - currentAmount;
    }
}
