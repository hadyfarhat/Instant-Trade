/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

// Standard Libraries
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

// External Libraries
import org.json.simple.JSONObject;
import org.apache.http.client.fluent.Request;
import org.json.simple.parser.ParseException;

// Custom Classes
import Models.Model;

/**
 * REST Web Service
 *
 * @author hadyfarhat
 */
@Path("")
public class Controller {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of InstantTrade
     */
    public Controller() {
    }

    
    /**
     * Gets all shares from shares json file
     * @param currency
     * @return JSONObject shares
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
    */
    @GET @Path("{currency}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShares(@PathParam("currency") String currency) throws ParseException, IOException {
       Model model = new Model(currency);
       JSONObject shares = model.getAllShares();
       return shares.toString();
    }
    
    
    /**
     * Gets a share based on the company symbol passed as a parameter
     * @param currency
     * @param companySymbol
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("share/{currency}/symbol/{companySymbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanySymbol(@PathParam("currency") String currency, @PathParam("companySymbol") String companySymbol) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject share = model.getShareDataByCompanySymbol(companySymbol);
        return share.toString();
    }
    
    
    /**
     * Gets a share based on company name
     * @param currency
     * @param companyName
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("share/{currency}/name/{companyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanyName(@PathParam("currency") String currency, @PathParam("companyName") String companyName) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject share = model.getShareDataByCompanyName(companyName);
        return share.toString();
    }
    
    
    /**
     * Get shares that have available greater than passed param
     * @param currency
     * @param numberOfShares
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/available/greater/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesGreaterAvailable(@PathParam("currency") String currency, @PathParam("numberOfShares") int numberOfShares) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesGreaterAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have available less than passed param
     * @param currency
     * @param numberOfShares
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/available/less/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesLessAvailable(@PathParam("currency") String currency, @PathParam("numberOfShares") int numberOfShares) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesLessAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have available equal to passed param
     * @param currency
     * @param numberOfShares
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/available/equal/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesEqualAvailable(@PathParam("currency") String currency, @PathParam("numberOfShares") int numberOfShares) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesEqualAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value less than passed param
     * @param currency
     * @param value
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/value/less/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesLessPriceValue(@PathParam("currency") String currency, @PathParam("value") int value) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesLessPriceValue(value);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value greater than passed param
     * @param currency
     * @param value
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/value/greater/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesGreaterPriceValue(@PathParam("currency") String currency, @PathParam("value") int value) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesGreaterPriceValue(value);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value equal to passed param
     * @param currency
     * @param value
     * @return JSONObject
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @GET @Path("shares/{currency}/value/equal/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesEqualPriceValue(@PathParam("currency") String currency, @PathParam("value") int value) throws ParseException, IOException {
        Model model = new Model(currency);
        JSONObject shares = model.getSharesEqualPriceValue(value);
        return shares.toString();
    }
    
 
    /**
     * Updates number of available shares
     * @param currency
     * @param companySymbol
     * @param numberOfShares
     * @return String status message
     * @throws org.json.simple.parser.ParseException
     * @throws java.io.IOException
     */
    @PUT @Path("buy")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String putJson(@PathParam("currency") String currency, @FormParam("companySymbol") String companySymbol, @FormParam("numberOfShares") int numberOfShares) throws ParseException, IOException {
        Model model = new Model(currency);
        return model.buyShares(companySymbol, numberOfShares);
    }
    
    
    @GET @Path("currencyconversion/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String testCurrencyConversion() throws IOException {
        String rate = Request.Get("http://localhost:8080/CurrencyConvertor/webresources/CurrencyConvertor").execute().returnContent().toString();
        return rate;
    }
    
}
