/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxapplication1;

/**
 *
 * @author Beleno, Itchon, Papas
 */
public class DataModel {
    private int userId;

    // Singleton pattern
    private static final DataModel instance = new DataModel();

    private DataModel() {
        // Private constructor to enforce singleton pattern
    }

    public static DataModel getInstance() {
        return instance;
    }

    public int getData() {
        return userId;
    }

    public void setData(int data) {
        this.userId = data;
    }
}
