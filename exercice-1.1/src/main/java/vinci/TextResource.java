package vinci;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Root resource (exposed at "myresource" path)
 */
@Singleton
@Path("texts")
public class TextResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Text> getAll(@DefaultValue ("") @QueryParam("level") String level) {
        List<Text> texts = be.vinci.Json.parse();
        if (!level.equals("")) {
            return texts.stream()
                    .filter(text -> text.getLevel().equals(level)).collect(Collectors.toList());
        }
        return texts;
    }

    // texts/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Text getOne(int id) {
        List<Text> texts = be.vinci.Json.parse();
        Text textFound = texts.stream().filter(text -> text.getId() == id).findAny().orElse(null);

        if (textFound == null)
            throw new jakarta.ws.rs.WebApplicationException(jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.NOT_FOUND)
                    .entity("Ressource not found").type("text/plain").build());
        return textFound;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Text createOne(Text text) {
        if (text == null || text.getContent() == null || text.getContent().isBlank())
            throw new jakarta.ws.rs.WebApplicationException(
                    jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST).entity("Lacks of mandatory info").type("text/plain").build());

        List<Text> texts = be.vinci.Json.parse();
        text.setId(texts.size() + 1);
        text.setContent(StringEscapeUtils.escapeHtml4(text.getContent()));

        texts.add(text);
        be.vinci.Json.serialize(texts);
        return text;
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Text deleteOne(@PathParam("id") int id) {
        List<Text> texts = be.vinci.Json.parse();
        Text textFound = texts.stream().filter(text -> text.getId() == id).findAny().orElse(null);

        if (textFound == null)
            throw new jakarta.ws.rs.WebApplicationException(jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.NOT_FOUND)
                    .entity("Ressource not found").type("text/plain").build());

        texts.remove(textFound);
        be.vinci.Json.serialize(texts);
        return textFound;
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Text updateOne(Text text, @PathParam("id") int id) {

        if (text == null || text.getContent() == null || text.getContent().isBlank() || text.getLevel() == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Lacks of mandatory info or unauthorized text level").type("text/plain").build());
        }

        var texts = be.vinci.Json.parse();
        Text textFound = texts.stream().filter(t -> t.getId() == id).findAny().orElse(null);
        if (textFound == null) {throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ressource not found").type("text/plain").build());
        }

        text.setId(texts.size() + 1);
        text.setContent(StringEscapeUtils.escapeHtml4(text.getContent()));
        texts.remove(text);
        texts.add(text);
        be.vinci.Json.serialize(texts);
        return text;
    }
}
