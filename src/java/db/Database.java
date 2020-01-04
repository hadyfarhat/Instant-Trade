/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


/**
 *
 * @author hadyfarhat
 */
public class Database {
    public Database() {
    }
    
    /**
     * Builds up an SQL string that will be used to create the Shares Table
     * @return SQL String
     */
    private String createShareTableSQLStatement() {
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
    
    /**
     * Creates a Connection object that connects to the database
     * @return Connection
     */
    private Connection connect() {
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
    
    /**
     * Selects all rows from the table Shares.
     * Adds this data to a HashMap
     * @return HashMap
     */
    public HashMap<Integer, HashMap<String, String>> getAllShares() {
        String sql = "SELECT * FROM Shares";
        
        HashMap<Integer, HashMap<String, String>> shares = new HashMap<Integer, HashMap<String, String>>();
        
        try {
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            // used as an index for the hashmap.
            // increments by 1 after going into next row in the query set
            int count = 0;
            
            // check if result set is not empty
            if (rs.next() != false) {
                do {
                    // loop through database and add data into a temp hashmap
                    HashMap<String, String> share = new HashMap<>();
                    share.put("companyName", rs.getString("companyName"));
                    share.put("companySymbol", rs.getString("companySymbol"));
                    share.put("available", rs.getString("availabe"));
                    share.put("priceValue", rs.getString("priceValue"));
                    share.put("priceCurrency", rs.getString("priceCurrency"));
                    // add the temp hashmap into the main hashmap shares
                    shares.put(count, share);
                    count++;
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return shares;
    }
    

    public static void main(String[] args) {
        Database db = new Database();
        HashMap<Integer, HashMap<String, String>> shares = db.getAllShares();
        for (int i : shares.keySet()) {
            HashMap<String, String> hm = shares.get(i);
//            for (String s : hm.keySet()) {
//                    System.out.println(s);
//                    System.out.println(hm.get(s));
//            }
            System.out.println(hm.get("companyName"));
	}
    }
}
