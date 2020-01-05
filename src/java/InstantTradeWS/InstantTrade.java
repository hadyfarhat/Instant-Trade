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
/**
 * REST Web Service
 *
 * @author hadyfarhat
 */
@Path("instanttrade")
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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllShares() {
       Data data = new Data();
       JSONObject shares = data.getAllShares();
       for(Iterator iterator = shares.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            System.out.println(shares.get(key));
            System.out.println("---------------");
        }
       return shares.toString();
    }
 
    /**
     * PUT method for updating or creating an instance of InstantTrade
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
