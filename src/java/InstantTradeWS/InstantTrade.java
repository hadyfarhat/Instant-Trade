/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantTradeWS;

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
import data.Data;
/**
 * REST Web Service
 *
 * @author hadyfarhat
 */
@Path("")
public class InstantTrade {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of InstantTrade
     */
    public InstantTrade() {
    }

    /**
     * Gets all shares from shares json file
     * @return JSONObject shares
     */
    @GET @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShares() {
       Data data = new Data();
       JSONObject shares = data.getAllShares();
       return shares.toString();
    }
    
    /**
     * Gets a Share based on the company symbol passed as a parameter
     * @return JSONObject
     */
    @GET @Path("share/symbol/{companySymbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanySymbol(@PathParam("companySymbol") String companySymbol) {
        Data data = new Data();
        JSONObject share = data.getShareDataByCompanySymbol(companySymbol);
        return share.toString();
    }
    
    /**
     * Gets a Share based on company name
     */
    @GET @Path("share/name/{companyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareByCompanyName(@PathParam("companyName") String companyName) {
        Data data = new Data();
        JSONObject share = data.getShareDataByCompanyName(companyName);
        return share.toString();
    }
    
    
    /**
     * Gets a Share that has available shares greater than passed param
     */
    @GET @Path("share/available/greater/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareGreaterAvailable(@PathParam("numberOfShares") int numberOfShares) {
        Data data = new Data();
        JSONObject shares = data.getShareGreaterAvailable(numberOfShares);
        return shares.toString();
    }
    
    
    /**
     * Gets a Share that has available shares less than passed param
     */
    @GET @Path("share/available/less/{numberOfShares}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShareLessAvailable(@PathParam("numberOfShares") int numberOfShares) {
        Data data = new Data();
        JSONObject shares = data.getShareLessAvailable(numberOfShares);
        return shares.toString();
    }
 
    /**
     * Updates number of available shares
     * @return String status message
     */
    @PUT @Path("buy")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String putJson(@FormParam("companySymbol") String companySymbol, @FormParam("numberOfShares") int numberOfShares) {
        Data data = new Data();
        return data.buyShares(companySymbol, numberOfShares);
    }
}
