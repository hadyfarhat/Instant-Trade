/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantTradeWS;

// Local Libraries
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

// Local Third Party Libraries
import org.json.simple.JSONObject;

// Local Custom Class
import data.Data;
import java.util.Iterator;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
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
    @GET @Path("share/{companySymbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getShare(@PathParam("companySymbol") String companySymbol) {
        Data data = new Data();
        JSONObject shareData = data.getShareData(companySymbol);
        return shareData.toString();
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
