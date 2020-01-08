/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

// Standard Libraries
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

// Custom Classes
import Model.Model;
import java.io.IOException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
     * @return JSONObject shares
     */
    @GET @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShares() {
       Model data = new Model();
       JSONObject shares = data.getAllShares();
       return shares.toString();
    }
    
    
    /**
     * Gets a share based on the company symbol passed as a parameter
     * @return JSONObject
     */
    @GET @Path("share/symbol/{companySymbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanySymbol(@PathParam("companySymbol") String companySymbol) {
        Model data = new Model();
        JSONObject share = data.getShareDataByCompanySymbol(companySymbol);
        return share.toString();
    }
    
    
    /**
     * Gets a share based on company name
     * @return JSONObject
     */
    @GET @Path("share/name/{companyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanyName(@PathParam("companyName") String companyName) {
        Model data = new Model();
        JSONObject share = data.getShareDataByCompanyName(companyName);
        return share.toString();
    }
    
    
    /**
     * Get shares that have available greater than passed param
     * @return JSONObject
     */
    @GET @Path("shares/available/greater/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesGreaterAvailable(@PathParam("numberOfShares") int numberOfShares) {
        Model data = new Model();
        JSONObject shares = data.getSharesGreaterAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have available less than passed param
     * @return JSONObject
     */
    @GET @Path("shares/available/less/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesLessAvailable(@PathParam("numberOfShares") int numberOfShares) {
        Model data = new Model();
        JSONObject shares = data.getSharesLessAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have available equal to passed param
     * @return JSONObject
     */
    @GET @Path("shares/available/equal/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesEqualAvailable(@PathParam("numberOfShares") int numberOfShares) {
        Model data = new Model();
        JSONObject shares = data.getSharesEqualAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Get shares based on the currency passed as a parameter
     * @return JSONObject
     */
    @GET @Path("shares/currency/{currency}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesByCurrency(@PathParam("currency") String currency) {
        Model data = new Model();
        JSONObject shares = data.getSharesByCurrency(currency);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value less than passed param
     * @return JSONObject
     */
    @GET @Path("shares/value/less/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesLessPriceValue(@PathParam("value") int value) {
        Model data = new Model();
        JSONObject shares = data.getSharesLessPriceValue(value);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value greater than passed param
     * @return JSONObject
     */
    @GET @Path("shares/value/greater/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesGreaterPriceValue(@PathParam("value") int value) {
        Model data = new Model();
        JSONObject shares = data.getSharesGreaterPriceValue(value);
        return shares.toString();
    }
    
    
    /**
     * Get shares that have price value equal to passed param
     * @return JSONObject
     */
    @GET @Path("shares/value/equal/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSharesEqualPriceValue(@PathParam("value") int value) {
        Model data = new Model();
        JSONObject shares = data.getSharesEqualPriceValue(value);
        return shares.toString();
    }
    
 
    /**
     * Updates number of available shares
     * @return String status message
     */
    @PUT @Path("buy")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String putJson(@FormParam("companySymbol") String companySymbol, @FormParam("numberOfShares") int numberOfShares) {
        Model data = new Model();
        return data.buyShares(companySymbol, numberOfShares);
    }
    
    
    @GET @Path("currencyconversion/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String testCurrencyConversion() throws IOException {
        String rate = Request.Get("http://localhost:8080/CurrencyConvertor/webresources/CurrencyConvertor").execute().returnContent().toString();
        return rate;
    }
    
}
