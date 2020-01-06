/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author hadyfarhat
 */
public class Data {
    
    private String dataFileName = "shares.json";
    private String dataFilePath = System.getProperty("user.dir") + "/" + this.dataFileName;
    
    public JSONObject getAllShares() {
        JSONObject shares = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(dataFilePath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            shares = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
            
        return shares;
    }
    
    
    /**
     * Loop through shares JSON file and search for the passed company symbol parameter
     * if found => return share data as a JSON object
     * if not found => return an empty JSON object
     * @param companySymbol
     * @return JSONObject
     */
    public JSONObject getShareData(String companySymbol) {
        JSONObject shareData = new JSONObject();
        
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (key.equals(companySymbol)) {
                shareData = (JSONObject) allShares.get(key);
                break;
            }
        }
        
        return shareData;
    }
    
    
    /**
     * Validation to check the passed company symbol parameter exists in shares data file
     * @param companySymbol
     * @return boolean
     */
    private boolean checkIfCompanySymbolExists(String companySymbol) {
        JSONObject companyData = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (key.equals(companySymbol)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * First check if company symbol exists
     * Second check if passed number of shares parameter is less than
     * company's available number of shares
     * if all is valid then subtract the passed number of shares parameter from
     * company's available number of shares
     * then save file
     * @param companySymbol
     * @param numberOfShares
     * @return String Error Message or 'OK' success message
     */
    public String buyShares(String companySymbol, int numberOfShares) {
        JSONObject allShares = this.getAllShares();
        JSONObject shareData = this.getShareData(companySymbol);
        
        if (shareData.size() == 0) {
            return "Share doesn't exist";
        }
        
        int availableCompanyShares = Integer.parseInt(shareData.get("available").toString());
        
        if (availableCompanyShares < numberOfShares) {
            return "Your requested shares amount is greater than the company's available shares";
        }

        shareData.put("available", availableCompanyShares - numberOfShares);
        allShares.put(shareData.get("companySymbol"), shareData);

        try {
            this.saveJsonObjectToFile(this.dataFilePath, allShares);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        return "OK";
    }
    
    
    private boolean saveJsonObjectToFile(String filepath, JSONObject obj) throws IOException {
        FileWriter file = null;
        try {
            file = new FileWriter(filepath);
            file.write(obj.toJSONString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            file.flush();
            file.close();
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        Data d = new Data();
        System.out.println(d.buyShares("AFC", 5));
    }
    
}
