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
     * Retrieves representation of an instance of InstantTradeWS.InstantTrade
     * @return an instance of java.lang.String
     */
    @GET @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShares() {
       Data data = new Data();
       JSONObject shares = data.getAllShares();
       return shares.toString();
    }
 
    /**
     * Updates number of available shares
     */
    @PUT @Path("buy")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String putJson(@FormParam("companySymbol") String companySymbol, @FormParam("numberOfShares") int numberOfShares) {
        Data data = new Data();
        data.buyShares(companySymbol, numberOfShares);
        return "Ok";
    }
}
