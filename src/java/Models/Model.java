/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

// Standard Libraries
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.http.client.fluent.Request;

// External Libraries
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author hadyfarhat
 */
public class Model {
    
    private final String dataFileName = "shares.json";
    private final String dataFilePath = System.getProperty("user.dir") + "/" + this.dataFileName;
    private final String apiKey = "MWROBPMAUOMA9TRG";
    
    
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
     * @return String shares json data
     */
    private String getAllSharesFromStorage() {
        JSONObject shares = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(this.dataFilePath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            shares = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
            
        return shares.toString();
    }
    
    
    /**
     * Updates shares json object with latest stock quotes
     * @return String shares json data
     */
    private String updateSharesWithLatestStockQuotes(String sharesStr) throws ParseException, IOException {
        System.out.println("hello");
        String url;
        JSONParser parser = new JSONParser();
        JSONObject shares = (JSONObject) parser.parse(sharesStr);
        // Loop through shares and call api to get latest quote for each company symbol
        for(Iterator iterator = shares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject share = (JSONObject) shares.get(key);
            System.out.println(share);
            url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + share.get("companySymbol") + "&apikey=" + this.apiKey;
            String request = Request.Get(url).execute().returnContent().toString();
            JSONObject requestParsed = (JSONObject) parser.parse(request);
            JSONObject realTimeShare = (JSONObject) requestParsed.get("Global Quote");
            double stockQuote = Double.parseDouble(realTimeShare.get("05. price").toString());
            JSONObject sharePrice = (JSONObject) share.get("sharePrice");
            sharePrice.put("value", stockQuote);
            share.put("sharePrice", sharePrice);
            System.out.println(share);
        }
        
        return shares.toString();
    }
    
    
    public JSONObject getAllShares() throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject shares = (JSONObject) parser.parse(this.getAllSharesFromStorage());
        JSONObject updatedSharesWithLatestStockQuotes = (JSONObject) parser.parse(this.updateSharesWithLatestStockQuotes(shares.toString()));
        return shares;
    }
    
    
    /**
     * Loop through shares JSON file and search for the passed company symbol parameter
     * @param companySymbol
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getShareDataByCompanySymbol(String companySymbol) throws ParseException, IOException {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getShareDataByCompanyName(String companyName) throws ParseException, IOException {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesGreaterAvailable(int numberOfShares) throws ParseException, IOException {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesLessAvailable(int numberOfShares) throws ParseException, IOException {
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
     * Loop through shares JSON file and search for a share that has available shares Equal to passed number of shares
     * Append each time a share is found to a json object
     * @param numberOfShares
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesEqualAvailable(int numberOfShares) throws ParseException, IOException {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            if (Integer.parseInt(temp.get("available").toString()) == numberOfShares) {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesByCurrency(String currency) throws ParseException, IOException {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesLessPriceValue(int value) throws ParseException, IOException {
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
     * Loop through shares JSON file and search for a share that has value equal to passed parameter
     * Append each time a share is found to a json object
     * @param value
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesEqualPriceValue(int value) throws ParseException, IOException {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            JSONObject tempSharePrice = (JSONObject) temp.get("sharePrice");
            if (Integer.parseInt(tempSharePrice.get("value").toString()) == value) {
                foundShares.put(temp.get("companySymbol"), temp);
            }
        }
        
        return foundShares;
    }
    
    
    /**
     * Loop through shares JSON file and search for a share that has value greater than passed parameter
     * Append each time a share is found to a json object
     * @param value
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject getSharesGreaterPriceValue(int value) throws ParseException, IOException {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();

        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject temp = (JSONObject) allShares.get(key);
            JSONObject tempSharePrice = (JSONObject) temp.get("sharePrice");
            if (Integer.parseInt(tempSharePrice.get("value").toString()) > value) {
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
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public String buyShares(String companySymbol, int numberOfShares) throws ParseException, IOException {
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
    
    
    public static void main(String[] args) throws ParseException, IOException {
        Model model = new Model();
        model.getAllShares();
        System.out.println("hello");
    }
    
}