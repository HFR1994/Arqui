package com.itesm.arqui.googlemaps.endpoint;

import com.itesm.arqui.googlemaps.domain.Pueblos;
import com.itesm.arqui.googlemaps.domain.Ruta;
import com.itesm.arqui.googlemaps.pojo.Result;
import com.itesm.arqui.googlemaps.service.PueblosService;
import com.itesm.arqui.googlemaps.pojo.templates.EndpointTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PueblosEndpoint extends EndpointTemplate<Pueblos> {

    @Autowired
    private PueblosService pueblosService;

    @GET
    @Path("/pueblos")
    @Produces("application/json")
    public Response getAgendas(){
        Result pueblosResult = pueblosService.getPueblos();
        return getResponse(pueblosResult);
    }


    @POST
    @Path("/ruta")
    @Produces("application/json")
    public Response getRoute(Ruta data){
        Result pueblosResult = pueblosService.getRoute(data);
        return getResponse(pueblosResult);
    }

    @GET
    @Path("/pueblo/{uuid}")
    @Produces("application/json")
    public Response getUserByUuid(@PathParam("uuid") String uuid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Optional<Pueblos> pueblos = pueblosService.getByUuid(uuid);
        Response response;
        Result pueblosResult = new Result();
        //noinspection Duplicates
        if (pueblos.isPresent()) {
            pueblosResult.setCode(Result.OK);
            pueblosResult.setMessage("¡Exito!");
            response = Response.ok(pueblosResult.getData(pueblos.get())).build();
        } else {
            pueblosResult.setCode(Result.BAD_REQUEST);
            pueblosResult.setMessage("¡Ups! No existe ese uuid de registro");
            response = Response.serverError().status(pueblosResult.getCode(), pueblosResult.getMessage()).entity(pueblosResult.getData(null)).build();
        }
        return response;
    }

    @POST
    @Path("/pueblo")
    @Produces("application/json")
    public Response insert(Pueblos data) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Result pueblosResult = pueblosService.insert(data);
        return getResponse(pueblosResult);
    }

    @PUT
    @Path("/pueblo")
    @Produces("application/json")
    public Response update(Pueblos data) {
        Result pueblosResult = pueblosService.update(data);
        return getResponse(pueblosResult);
    }

    @DELETE
    @Path("/pueblo")
    @Produces("application/json")
    public Response delete(Pueblos data) {
        Result pueblosResult = pueblosService.delete(data);
        return getResponse(pueblosResult);
    }

    private Response getResponse(Result pueblosResult) {
        Response response;
        if (pueblosResult.getCode().equals(Result.CREATED) || pueblosResult.getCode().equals(Result.OK)) {
            response = Response.ok(pueblosResult.getData()).build();
        } else {
            response = Response.serverError().status(pueblosResult.getCode(), pueblosResult.getMessage()).entity(pueblosResult.getData()).build();
        }
        return response;
    }

}
