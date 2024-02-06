package be.vinci.services;

import be.vinci.domain.Page;
import be.vinci.domain.User;
import be.vinci.services.utils.Json;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PageDataService {
    private static final String COLLECTION_NAME = "Pages";
    private static final Json<Page> jsonDB = new Json<>(Page.class);

    public List<Page> getAll() {
        List<Page> items = jsonDB.parse(COLLECTION_NAME);
        return items.stream()
                .filter(item -> item.getStatusPublication().equals("published"))
                .collect(Collectors.toList());
    }
    public List<Page> getAll(User authUser){
        List<Page> items = jsonDB.parse(COLLECTION_NAME);
        return items.stream()
                .filter(item -> item.getStatusPublication().contentEquals("published") || item.getAuthorId() == authUser.getId())
                .collect(Collectors.toList());
    }

    public Page getOne(int id) {
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);
        return pages.stream().filter(item -> (item.getId() == id)
                        && (item.getStatusPublication().contentEquals("published")))
                .findAny().orElse(null);
    }

    public Page getOne(int id, User authenticatedUser) {
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);
        return pages.stream()
                .filter(item -> (item.getId() == id) && (item.getStatusPublication().contentEquals("published") || item.getAuthorId() == authenticatedUser.getId()))
                .findAny()
                .orElse(null);
    }

    public Page createOne(Page page,User authenticatedUser){
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);

        page.setId(nexPageId());
        page.setTitle(StringEscapeUtils.escapeHtml4(page.getTitle()));
        page.setUrl(StringEscapeUtils.escapeHtml4(page.getUrl()));
        page.setContent(StringEscapeUtils.escapeHtml4(page.getContent()));
        page.setAuthor(authenticatedUser.getLogin());
        page.setAuthorId(authenticatedUser.getId());


        pages.add(page);

        jsonDB.serialize(pages, COLLECTION_NAME);
        return page;
    }

    public static int nexPageId() {
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);
        if (pages.isEmpty()) {
            return 1;
        }
        return pages.get(pages.size() - 1).getId() + 1;
    }

    public Page deleteOne(int id, User authenticatedUser){
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);
        Page pageToDelete = pages.stream()
                .filter(item -> item.getId() == id)
                .findAny()
                .orElse(null);

        if (pageToDelete == null) return null;
        if (pageToDelete.getAuthorId() != authenticatedUser.getId()) throw new IllegalStateException("Forbidden");

        pages.remove(pageToDelete);
        jsonDB.serialize(pages, COLLECTION_NAME);
        return pageToDelete;
    }


    public Page updateOne(Page page, int id, User authenticatedUser) {
        Page pageToUpdate = getOne(id, authenticatedUser);
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);

        if (pageToUpdate == null) return null;
        if (pageToUpdate.getAuthorId() != authenticatedUser.getId()) throw new IllegalStateException("Forbidden");

        pageToUpdate.setId(id);

        // escape dangerous chars to protect against XSS attacks
        if (page.getTitle() != null) {
            pageToUpdate.setTitle(StringEscapeUtils.escapeHtml4(page.getTitle()));
        }
        if (page.getUrl() != null) {
            pageToUpdate.setUrl(StringEscapeUtils.escapeHtml4(page.getUrl()));
        }
        if (page.getContent() != null) {
            pageToUpdate.setContent(StringEscapeUtils.escapeHtml4(page.getContent()));
        }
        if (page.getAuthorId() != 0) {
            pageToUpdate.setAuthorId(page.getAuthorId());
        }
        if (page.getStatusPublication() != null) {
            pageToUpdate.setStatusPublication(page.getStatusPublication());
        }

        pages.remove(page);
        pages.add(pageToUpdate);
        jsonDB.serialize(pages, COLLECTION_NAME);

        return pageToUpdate;
    }


    public int nextPageId() {
        List<Page> pages = jsonDB.parse(COLLECTION_NAME);
        if (pages.isEmpty()) {
            return 1;
        }
        return pages.get(pages.size() - 1).getId() + 1;
    }
}
