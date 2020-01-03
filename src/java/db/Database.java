/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author hadyfarhat
 */
public class Database {
    public Database() {
    }
    
    public String createShareTableSQLStatement() {
        String sql = "CREATE TABLE IF NOT EXISTS Shares (\n"
                + "companyName VARCHAR(255) NOT NULL,\n"
                + "companySymbol VARCHAR(255) NOT NULL,\n"
                + "availabe INTEGER NOT NULL,\n"
                + "priceValue DECIMAL NOT NULL,\n"
                + "priceCurrency CHAR NOT NULL,\n"
                + "PRIMARY KEY (companySymbol),\n"
                + "UNIQUE (companySymbol)\n"
                + ");";
        return sql;
    }
    
    public Connection connect() {
        Connection conn = null;
        
        try {
            String databaseName = "instanttrade.db";
            String filePath = System.getProperty("user.dir") + "/src/java/db/" + databaseName;
            // create connection
            conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);
            System.out.println("Connection has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    // testing
    public static void main(String[] args) {
        Database db = new Database();
        Connection conn = null;
        
        try {
            conn = db.connect();
            String sql = db.createShareTableSQLStatement();
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
