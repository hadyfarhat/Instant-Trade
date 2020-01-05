/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author hadyfarhat
 */
public class Data {
//     String databaseName = "instanttrade.db";
////            String filePath = System.getProperty("user.dir") + "/src/java/db/" + databaseName;
//    String filePath = System.getProperty("user.dir") + "/" +databaseName;
//    System.out.println("User dir: " + System.getProperty("user.dir"));
//    File f = new File(filePath);
//    System.out.println(f.exists());
    
    public JSONObject getAllShares() {
        JSONObject shares = new JSONObject();
        String dataFileName = "data.json";
        String dataFilePath = System.getProperty("user.dir") + "/" + dataFileName;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(dataFilePath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            shares = (JSONObject) obj;
            System.out.println(shares);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
            
        return shares;
    }
}
