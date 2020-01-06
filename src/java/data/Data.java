/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

// Standard Libraries
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// External Libraries
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
    
    
    /**
     * Gets the current date and time
     * @return String formatted date
     */
    private String getCurrentDateTime() {
        LocalDateTime date = LocalDateTime.now();
	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = date.format(dateFormat);
        
        return formattedDate;
    }
    
    
    /**
     * Saves the passed json object parameter into the filepath passed as a parameter
     * @param String filepath
     * @param JSONObject
     * @return boolean if saved or not
     */
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
    
    
    /**
     * Uses Json Parser to parse shares data json file into a json object
     * @return JSONObject shares data
     */
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
     * @param companySymbol
     * @return JSONObject
     */
    public JSONObject getShareDataByCompanySymbol(String companySymbol) {
        JSONObject shareData = new JSONObject();
        JSONObject allShares = this.getAllShares();
        
        companySymbol = companySymbol.toUpperCase();

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
     * Loop through shares JSON file and search for the passed company name parameter
     * @param companyName
     * @return JSONObject
     */
    public JSONObject getShareDataByCompanyName(String companyName) {
        JSONObject shareData = new JSONObject();
        JSONObject allShares = this.getAllShares();
        
        // Capitalise first letter and lowercase the rest
        companyName = companyName.substring(0, 1).toUpperCase() + companyName.substring(1).toLowerCase();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            if (temp.get("companyName").equals(companyName)) {
                shareData = temp;
                break;
            }
        }
        
        return shareData;
    }
    
    
    /**
     * Loop through shares JSON file and search for a share that has available shares greater than passed number of shares
     * Append each time a share is found to a json object
     * @param numberOfShares
     * @return JSONObject
     */
    public JSONObject getSharesGreaterAvailable(int numberOfShares) {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            if (Integer.parseInt(temp.get("available").toString()) > numberOfShares) {
                foundShares.put(temp.get("companySymbol"), temp);
            }
        }
        
        return foundShares;
    }
    

    /**
     * Loop through shares JSON file and search for a share that has available shares less than passed number of shares
     * Append each time a share is found to a json object
     * @param numberOfShares
     * @return JSONObject
     */
    public JSONObject getSharesLessAvailable(int numberOfShares) {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            if (Integer.parseInt(temp.get("available").toString()) < numberOfShares) {
                foundShares.put(temp.get("companySymbol"), temp);
            }
        }
        
        return foundShares;
    }
    
    
    /**
     * Loop through shares JSON file and search for a share that has a currency same as the one passed as a parameter
     * Append each time a share is found to a json object
     * @param currency
     * @return JSONObject
     */
    public JSONObject getSharesByCurrency(String currency) {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            JSONObject tempSharePrice = (JSONObject) temp.get("sharePrice");
            if (tempSharePrice.get("currency").equals(currency)) {
                foundShares.put(temp.get("companySymbol"), temp);
            }
        }
        
        return foundShares;
    }
    
    
     /**
     * Loop through shares JSON file and search for a share that has value less than passed parameter
     * Append each time a share is found to a json object
     * @param value
     * @return JSONObject
     */
    public JSONObject getSharesLessPriceValue(int value) {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            JSONObject tempSharePrice = (JSONObject) temp.get("sharePrice");
            if (Integer.parseInt(tempSharePrice.get("value").toString()) < value) {
                foundShares.put(temp.get("companySymbol"), temp);
            }
        }
        
        return foundShares;
    }

    
    /**
     * First check if company symbol exists
     * Second check if passed number of shares parameter is less than company's available number of shares
     * If all is valid then subtract the passed number of shares parameter from company's available number of shares
     * Lastly, save updated json object to file
     * @param companySymbol
     * @param numberOfShares
     * @return String Error Message or 'OK' success message
     */
    public String buyShares(String companySymbol, int numberOfShares) {
        JSONObject allShares = this.getAllShares();
        JSONObject shareData = this.getShareDataByCompanySymbol(companySymbol);
        
        if (shareData.isEmpty()) {
            return "Share doesn't exist";
        }
        
        int availableCompanyShares = Integer.parseInt(shareData.get("available").toString());
        
        if (availableCompanyShares < numberOfShares) {
            return "Your requested shares amount is greater than the company's available shares";
        }
        
        String currentDateTime = this.getCurrentDateTime();
        shareData.put("available", availableCompanyShares - numberOfShares);
        shareData.put("lastUpdated", currentDateTime);
        allShares.put(shareData.get("companySymbol"), shareData);

        try {
            this.saveJsonObjectToFile(this.dataFilePath, allShares);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        return "OK";
    }
    
    
    public static void main(String[] args) {
        Data d = new Data();
        System.out.println(d.getSharesByCurrency("£"));
    }
    
}
