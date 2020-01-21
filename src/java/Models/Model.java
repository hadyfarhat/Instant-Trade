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

// External Libraries
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.http.client.fluent.Request;
import org.json.simple.JSONArray;

/**
 *
 * @author hadyfarhat
 */
public class Model {
    
    private final String currency;
    private final String sharesFileName = "shares.json";
    private final String sharesFilePath = System.getProperty("user.dir") + "/" + this.sharesFileName;
    private final String usersFileName = "users.json";
    private final String usersFilePath = System.getProperty("user.dir") + "/" + this.usersFileName;
    private final String apiKey = "OjY0ODI0YzE5OWViNTIzNjRlMjBjMjQxMDRlM2ZkZWU3";
    
    
    /**
     * Constructor. The passed currency parameter specifies which currency to be used in every method call
     * 
     * @param currency
     */
    public Model(String currency) {
        this.currency = currency;
    }
    
    
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
     * Converts a json file into a JSONObject
     * @param filepath
     * @return JSONObject
     */
    private JSONObject convertJsonFileToObject(String filepath) {
        JSONObject obj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(filepath)) {
            //Read JSON file
            obj = (JSONObject) jsonParser.parse(reader);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
            
        return obj;
    }
    
    
    /**
     * Uses Json Parser to parse shares data json file into a json object
     * @return String shares json data
     */
    private String getAllSharesFromStorage() {
        JSONObject shares = this.convertJsonFileToObject(this.sharesFilePath);
        return shares.toString();
    }
    
    
    /**
     * Gets a share from shares.json file
     * @param companySymbol
     * @return
     * @throws ParseException 
     */
    private String getShareFromStorage(String companySymbol) throws ParseException {
        JSONObject share = new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject allShares = (JSONObject) parser.parse(this.getAllSharesFromStorage());
        
        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (key.equals(companySymbol)) {
                share = (JSONObject) allShares.get(key);
            }
            
        }
        
        return share.toString();
    }
    
    
    /**
     * Updates shares json object with latest stock quotes
     * @return String shares json data
     */
    private String updateSharesWithLatestStockQuotes(String sharesStr) throws ParseException, IOException {
        String url;
        JSONParser parser = new JSONParser();
        JSONObject shares = (JSONObject) parser.parse(sharesStr);
        
        // Loop through shares and call api to get latest quote for each company symbol
        for(Iterator iterator = shares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject share = (JSONObject) shares.get(key);
            
            // get real time stock prices
            url = "https://api-v2.intrinio.com/securities/" + share.get("companySymbol") + "/prices?api_key=" + this.apiKey + "&page_size=1";
            String request = Request.Get(url).execute().returnContent().toString();
       
            // parse the request into a json object
            JSONObject requestParsed = (JSONObject) parser.parse(request);
            // get stock prices
            JSONArray realTimeStocks = (JSONArray) requestParsed.get("stock_prices");
            JSONObject realTimeStock = (JSONObject) realTimeStocks.get(0);
            // get security
            JSONObject security = (JSONObject) requestParsed.get("security");
            
            // get stock quote value from stock prices
            double stockQuote = Double.parseDouble(realTimeStock.get("close").toString());
            // get currency from security
            String currency = security.get("currency").toString();
       
            // update share json object with the stock quote value and currency
            JSONObject sharePrice = (JSONObject) share.get("sharePrice");
            sharePrice.put("value", stockQuote);
            sharePrice.put("currency", currency);
            share.put("sharePrice", sharePrice);
        }
        
        return shares.toString();
    }
    
    
    /**
     * Call api to get the latest currency conversion rate
     * Apply this rate for each share price value
     * @param toCurrency
     * @param sharesStr
     * @return String shares json
     * @throws ParseException 
     */
    private String updateSharesWithLatestCurrencyConversionRate(String toCurrency, String sharesStr) throws ParseException, IOException {
        String url;
        JSONParser parser = new JSONParser();
        JSONObject shares = (JSONObject) parser.parse(sharesStr);
        
        for(Iterator iterator = shares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject share = (JSONObject) shares.get(key);
            JSONObject sharePrice = (JSONObject) share.get("sharePrice");
            Double sharePriceValue = Double.parseDouble(sharePrice.get("value").toString());
            String sharePriceCurrency = (String) sharePrice.get("currency");
            
            // convert share price value to the requested currency
            url = "http://localhost:8080/CurrencyConvertor/webresources/exchange-rate/" + sharePriceValue + "/" + sharePriceCurrency + "/" + toCurrency;
            String request = Request.Get(url).execute().returnContent().toString();
            Double convertedSharePriceValue = Double.parseDouble(request);
            
            // update share json object with the calculated share price value
            sharePrice.put("value", convertedSharePriceValue);
            sharePrice.put("currency", toCurrency);
            share.put("sharePrice", sharePrice);
            shares.put(share.get("companySymbol"), share);
        }
        
        return shares.toString();
    }
    
    
    /**
     * Gets shares from saved json file
     * For each share => Call intrinio API to get the latest share price value and currency
     * For each share => Call Currency Convertor API to convert share price value to the specified currency
     * @return String shares json
     * @throws ParseException
     * @throws IOException 
     */
    public JSONObject getAllShares() throws ParseException, IOException {
        // return new JSONObject();
        JSONParser parser = new JSONParser();
        JSONObject shares = (JSONObject) parser.parse(this.getAllSharesFromStorage());
        JSONObject sharesWithLatestStockQuotes = (JSONObject) parser.parse(this.updateSharesWithLatestStockQuotes(shares.toString()));
        JSONObject sharesWithCurrencyConversion = (JSONObject) parser.parse(this.updateSharesWithLatestCurrencyConversionRate(this.currency, sharesWithLatestStockQuotes.toString()));
        return sharesWithCurrencyConversion;
    }
    
    
    /**
     * Loop through each share and apply the conditions passed as parameters
     * @param sharePriceValueSearchType
     * @param sharePriceValue
     * @param availableSharesSearchType
     * @param availableShares
     * @param companySymbol
     * @param companyName
     * @return JSONObject of shares found
     * @throws ParseException
     * @throws IOException 
     */
    public JSONObject getSharesBySearch(String sharePriceValueSearchType,
                                        String sharePriceValue,
                                        String availableSharesSearchType,
                                        String availableShares,
                                        String companySymbol,
                                        String companyName) throws ParseException, IOException {
        JSONObject foundShares = new JSONObject();
        JSONObject allShares = this.getAllShares();
        
        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject share = (JSONObject) allShares.get(key);
            JSONObject sharePrice = (JSONObject) share.get("sharePrice");
            
            boolean valid = true;
            
            // Share Price conditions
            if (sharePriceValueSearchType != null && !sharePriceValueSearchType.isEmpty()) {
                if (sharePriceValueSearchType.equals("greaterThan")) {
                    if (Double.parseDouble(sharePrice.get("value").toString()) <= Double.parseDouble(sharePriceValue)) {
                        valid = false;
                    }
                } else if (sharePriceValueSearchType.equals("lessThan")) {
                    if (Double.parseDouble(sharePrice.get("value").toString()) >= Double.parseDouble(sharePriceValue)) {
                        valid = false;
                    }
                } else if (sharePriceValueSearchType.equals("equalTo")) {
                    if (Double.parseDouble(sharePrice.get("value").toString()) != Double.parseDouble(sharePriceValue)) {
                        valid = false;
                    }
                }
            }
            
            // Available Number of Shares conditions
            if (availableSharesSearchType != null && !availableSharesSearchType.isEmpty()) {
                if (availableSharesSearchType.equals("greaterThan")) {
                    if (Double.parseDouble(share.get("available").toString()) <= Double.parseDouble(availableShares)) {
                        valid = false;
                    }
                } else if (availableSharesSearchType.equals("lessThan")) {
                    if (Double.parseDouble(share.get("available").toString()) >= Double.parseDouble(availableShares)) {
                        valid = false;
                    }
                } else if (availableSharesSearchType.equals("equalTo")) {
                    if (Double.parseDouble(share.get("available").toString()) != Double.parseDouble(availableShares)) {
                        valid = false;
                    }
                }   
            }
            
            // Company Name Condition
            if (companyName != null && !companyName.isEmpty()) {
                if (!share.get("companyName").equals(companyName)) {
                    valid = false;
                }
            }
            
            // Company Symbol Condition
            if (companySymbol != null && !companySymbol.isEmpty()) {
                if (!share.get("companySymbol").equals(companySymbol)) {
                    valid = false;
                }
            }
            
            // if all conditions satisfy => append share to foundShares
            if (valid) {
                foundShares.put(share.get("companySymbol"), share);
            }
            
        }
        
        return foundShares;
    }
    
    
    /**
     * Get the share with either one of the following types:
     * - Highest price value
     * - Lowest price value
     * - Highest number of available shares
     * - Lowest number of available shares
     * @param type
     * @return JSONObject found share
     * @throws org.json.simple.parser.ParseException 
     * @throws java.io.IOException 
     */
    public JSONObject simpleSearch(String type) throws ParseException, IOException {
        JSONObject foundShare;
        JSONObject allShares = this.getAllShares();
        
        // assign foundShare to first share in allShares
        String firstKey = (String) allShares.keySet().toArray()[0];
        foundShare = (JSONObject) allShares.get(firstKey);
        JSONObject foundSharePrice = (JSONObject) foundShare.get("sharePrice");
        
        for(Iterator iterator = allShares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject currentShare = (JSONObject) allShares.get(key);
            JSONObject currentSharePrice = (JSONObject) currentShare.get("sharePrice");
            
            if (type.equals("highestPriceValue")) {
                Double currentSharePriceValue = Double.parseDouble(currentSharePrice.get("value").toString());
                Double foundSharePriceValue = Double.parseDouble(foundSharePrice.get("value").toString());
                if (currentSharePriceValue > foundSharePriceValue) {
                    foundShare = currentShare;
                    foundSharePrice = (JSONObject) currentShare.get("sharePrice");
                }
            } else if (type.equals("lowestPriceValue")) {
                Double currentSharePriceValue = Double.parseDouble(currentSharePrice.get("value").toString());
                Double foundSharePriceValue = Double.parseDouble(foundSharePrice.get("value").toString());
                if (currentSharePriceValue < foundSharePriceValue) {
                    foundShare = currentShare;
                    foundSharePrice = (JSONObject) currentShare.get("sharePrice");
                }
            } else if (type.equals("highestAvailableNumberOfShares")) {
                Double currentShareNumOfAvailableShares = Double.parseDouble(currentShare.get("available").toString());
                Double foundShareNumOfAvailableShares = Double.parseDouble(foundShare.get("available").toString());
                if (currentShareNumOfAvailableShares > foundShareNumOfAvailableShares) {
                    foundShare = currentShare;
                }
            } else if (type.equals("lowestAvailableNumberOfShares")) {
                Double currentShareNumOfAvailableShares = Double.parseDouble(currentShare.get("available").toString());
                Double foundShareNumOfAvailableShares = Double.parseDouble(foundShare.get("available").toString());
                if (currentShareNumOfAvailableShares < foundShareNumOfAvailableShares) {
                    foundShare = currentShare;
                }
            }
        }
        
        JSONObject share = new JSONObject();
        share.put(foundShare.get("companySymbol"), foundShare);
        return share;
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
     * First check if company symbol exists
     * Second check if passed number of shares parameter is less than company's available number of shares
     * If all is valid then subtract the passed number of shares parameter from company's available number of shares
     * Update the user data by adding the number of shares bought
     * @param companySymbol
     * @param numberOfShares
     * @return json object share that has been updated or an empty json object if there were errors
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    public JSONObject buyShares(String companySymbol, int numberOfShares, String username) throws ParseException, IOException {
        JSONObject allShares = this.getAllShares();
        JSONObject share = this.getShareDataByCompanySymbol(companySymbol);
        
        if (share.isEmpty()) {
            return new JSONObject();
        }
        
        int availableCompanyShares = Integer.parseInt(share.get("available").toString());
        
        if (availableCompanyShares < numberOfShares) {
            return new JSONObject();
        }
        
        // Update company shares
        String currentDateTime = this.getCurrentDateTime();
        share.put("available", availableCompanyShares - numberOfShares);
        share.put("lastUpdated", currentDateTime);
        allShares.put(share.get("companySymbol"), share);
        
        // Update user shares
        JSONObject allUsers = this.getAllUsers();
        JSONObject user = this.getUser(username);
        JSONObject userShares = (JSONObject) user.get("shares");
        
        int userNumberOfShares = 0;
        if (userShares.containsKey(companySymbol)) {
            userNumberOfShares = Integer.parseInt(userShares.get(companySymbol).toString());
        }
        userNumberOfShares += numberOfShares;
        
        userShares.put(companySymbol, userNumberOfShares);
        user.put("shares", userShares);
        allUsers.put(username, user);
        

        try {
            this.saveJsonObjectToFile(this.sharesFilePath, allShares);
            this.saveJsonObjectToFile(this.usersFilePath, allUsers);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        return share;
    }
    
    
    /**
     * Goes through the user json file and checks if username and password match any data
     * @param username
     * @param password
     * @return boolean
     */
    public boolean validateUser(String username, String password) {
        JSONObject users = this.convertJsonFileToObject(usersFilePath);
        for(Iterator iterator = users.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject user = (JSONObject) users.get(key);
            if (user.get("username").equals(username) && user.get("password").equals(password)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * Goes through the user json file and checks if username exists
     * @param username
     * @return boolean 
     */
    private boolean checkIfUsernameExists(String username) {
        JSONObject users = this.convertJsonFileToObject(usersFilePath);
        for(Iterator iterator = users.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject user = (JSONObject) users.get(key);
            if (user.get("username").equals(username)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * Searches for user by username and returns it as a json object
     * @return JSONObject user
     */
    private JSONObject getUser(String username) {
        JSONObject users = this.convertJsonFileToObject(usersFilePath);

        for(Iterator iterator = users.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject user = (JSONObject) users.get(key);
            if (user.get("username").equals(username)) {
                return user;
            }
        }
        
        return new JSONObject();
    }
    
    
    /**
     * Gets all users from json file
     * @return JSONObject
     */
    private JSONObject getAllUsers() {
        JSONObject users = this.convertJsonFileToObject(usersFilePath);
        return users;
    }
    
    
    /**
     * Goes through the user json file and checks if username and password match any data
     * if it doesn't => add it
     * @param username
     * @param password
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean registerUser(String username, String password) throws IOException {        
        if (!this.checkIfUsernameExists(username)) {
            JSONObject users = this.convertJsonFileToObject(usersFilePath);
            JSONObject user = new JSONObject();
            JSONObject shares = new JSONObject();
            user.put("username", username);
            user.put("password", password);
            user.put("shares", shares);
            users.put(username, user);
            System.out.println("User: " + user);
            this.saveJsonObjectToFile(this.usersFilePath, users);
            return true;
        }
        
        return false;
    }
    
    
    /**
     * Gets the shares of a user
     * @param username
     * @return JSONOjbect shares
     */
    public JSONObject getUserShares(String username) {
        JSONObject userShares = new JSONObject();
        
        JSONObject users = this.convertJsonFileToObject(usersFilePath);
        for(Iterator iterator = users.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject user = (JSONObject) users.get(key);
            if (user.get("username").equals(username)) {
                userShares = (JSONObject) user.get("shares");
                return userShares;
            }
        }
        
        return userShares;
    }
    
    
    /**
     * Validates that user has shares in a company
     * @param username
     * @param companySymbol
     * @return 
     */
    private boolean validateUserHasCompanyShares(String username, String companySymbol) {
        JSONObject userShares = this.getUserShares(username);
        if (userShares.size() > 0) {
            for(Iterator iterator = userShares.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                if (key.equals(companySymbol)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    /**
     * Adds shares to a company
     * @param companySymbol
     * @param numOfShares
     */
    private void addCompanyShares(String companySymbol, int numOfShares) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        // get share
        JSONObject share = (JSONObject) parser.parse(this.getShareFromStorage(companySymbol));
        // get all shares
        JSONObject allShares = (JSONObject) parser.parse(this.getAllSharesFromStorage());
        // update num of shares
        int availableShares = Integer.parseInt(share.get("available").toString());
        share.put("available", availableShares + numOfShares);
        allShares.put(share.get("companySymbol"), share);
        // save shares to file
        this.saveJsonObjectToFile(this.sharesFilePath, allShares);
    }
    

    /**
     * - Lookup username in users.json
     *      - validate username exists
     *      - validate user has any number of shares
     *      - validate user has shares in the company
     *      - validate numOfShares less than or equal to user numOfShares
     * - Subtract numOfShares from user
     * - Add subtracted numOfShares to the share in shares.json
     * @param username
     * @param companySymbol
     * @param numOfShares
     * @return json:
     *      - {"status": "OK", "shares": {shares}, "userShares": {userShares}}
     *      - {"status": "Err", "errorMessage": "message"}
     * @throws java.io.IOException
     * @throws org.json.simple.parser.ParseException
     */
    public JSONObject sellShares(String username, String companySymbol, int numOfShares) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject status = new JSONObject();
        JSONObject allUsers = this.getAllUsers();
        JSONObject user = (JSONObject) allUsers.get(username);
        
        // validate username exists
        boolean usernameExists = this.checkIfUsernameExists(username);
        if (!usernameExists) {
            status.put("status", "Err");
            status.put("errorMessage", "Username does not exist");
            return status;
        }
        
        // validate userShares num > 0
        JSONObject userShares = this.getUserShares(username);
        if (userShares.isEmpty()) {
            status.put("status", "Err");
            status.put("errorMessage", "User doesn't have any share");
            return status;
        }
        
        // validate user has shares in the company
        boolean userHasSharesInCompany = this.validateUserHasCompanyShares(username, companySymbol);
        if (!userHasSharesInCompany) {
            status.put("status", "Err");
            status.put("errorMessage", "User does not have shares in this company");
            return status;
        } 

        // validate numOfShares param <= numOfShares in user
        int userCompanyShares = Integer.parseInt(userShares.get(companySymbol).toString());
        if (numOfShares > userCompanyShares) {
            status.put("status", "Err");
            status.put("errorMessage", "Attempting to sell invalid number of shares");
            return status;
        }
        
        // subtract numOfShares from user shares and save to file
        userShares.put(companySymbol, userCompanyShares - numOfShares);
        user.put("shares", userShares);
        allUsers.put(username, user);
        this.saveJsonObjectToFile(usersFilePath, allUsers);
        
        // add subtracted numOfShares to company and save to file
        this.addCompanyShares(companySymbol, numOfShares);

        status.put("status", "OK");
        status.put("userShares", userShares);
        return status;
    }
    
    
    public static void main(String[] args) throws ParseException, IOException {
        Model model = new Model("GBP");
        System.out.println(model.sellShares("user2", "AAPL", 100));
    }
    
}
