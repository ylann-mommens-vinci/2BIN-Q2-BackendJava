package be.vinci.api;

import be.vinci.api.filters.Authorize;
import be.vinci.domain.Page;
import be.vinci.domain.User;
import be.vinci.services.PageDataService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;

import java.util.List;

@Singleton
@Path("pages")
public class PageResource {
    private PageDataService myPageDataService = new PageDataService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Page> readAll (@Context ContainerRequest request) {
        User authenticatedUser = (User) request.getProperty("user");

        if (authenticatedUser == null) return myPageDataService.getAll();

        // deals with authenticated requests
        return myPageDataService.getAll(authenticatedUser);
    }

    @Authorize
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page readOne(@PathParam("id") int id, @Context ContainerRequest request) {
        User authenticatedUser = (User) request.getProperty("user");
        Page pageFound = null;

        if (authenticatedUser == null) {
            pageFound =  myPageDataService.getOne(id);
        }else {
            pageFound = myPageDataService.getOne(id, authenticatedUser);
        }

        if (pageFound == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ressource not found").type("text/plain").build());

        return pageFound;
    }
    @Authorize
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Page createOne(Page page, @Context ContainerRequest request) {
        if (page == null || page.getTitle() == null || page.getTitle().isEmpty()
                || page.getContent() == null || page.getContent().isEmpty()
                || page.getStatusPublication() == null
                || page.getUrl() == null || page.getUrl().isEmpty()) {
            throw new WebApplicationException("Lacks of mandatory info", Response.Status.BAD_REQUEST);
        }

        User authenticatedUser = (User) request.getProperty("user");
        return myPageDataService.createOne(page, authenticatedUser);
    }

    @Authorize
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page deleteOne(@PathParam("id") int id, @Context ContainerRequest request){
        if(id == 0) throw new WebApplicationException("Lacks of mandatory id info", Response.Status.BAD_REQUEST);

        User authUser = (User) request.getProperty("user");
        Page deletedPage=null;
        try {
            deletedPage = myPageDataService.deleteOne(id,authUser);
        }catch (Exception e) {
            throw new WebApplicationException("", Response.Status.FORBIDDEN);
        }

        return deletedPage;
    }

    @Authorize
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page updateOne(@PathParam("id") int id,Page page, @Context ContainerRequest request){
        if (page == null || page.getTitle() == null || page.getTitle().isEmpty()
                || page.getContent() == null || page.getContent().isEmpty()
                || page.getStatusPublication() == null
                || page.getUrl() == null || page.getUrl().isEmpty()) {
            throw new WebApplicationException("Lacks of mandatory info", Response.Status.BAD_REQUEST);
        }

        User authUser = (User) request.getProperty("user");
        Page updatedPage = null;
        try {
            updatedPage = myPageDataService.updateOne(page, id, authUser);
        }catch (Exception e){
            throw new WebApplicationException("Your are not the author", Response.Status.FORBIDDEN);
        }

        return updatedPage;
    }
}
