/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantTradeWS;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

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
    public String getJson() {
        //TODO return proper representation object
//        throw new UnsupportedOperationException();
        JSONObject obj = new JSONObject();
        String text;
        
        obj.put("name", "hadi");
        obj.put("age", 22);
        
        text = obj.toString();
        return text;
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
