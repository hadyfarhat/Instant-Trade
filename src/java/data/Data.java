/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

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
            System.out.println(e.getMessage());
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
            
        return shares;
    }
    
}
