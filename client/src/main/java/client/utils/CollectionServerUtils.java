package client.utils;


import commons.Collection;
import commons.CollectionNote;
import commons.Note;
import commons.Pair;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class CollectionServerUtils {
    private final String server;
    

    public String getServer(){
       return this.server;
   }


    public CollectionServerUtils(String server) {
        this.server = server;
    }

    public CollectionServerUtils() {
        this.server = "http://localhost:8080/";
    }

    public static CollectionServerUtils[] getFromConfig(Config config) {
        return config
                .getAllServers()
                .stream()
                .map(CollectionServerUtils::new)
                .toArray(CollectionServerUtils[]::new);
    }

    public static void main(String[] args) {

        CollectionServerUtils collectionServerUtils = new CollectionServerUtils("http://localhost:8080/");
        collectionServerUtils.addTestCollection();

        var ids = collectionServerUtils.getAllCollectionNameIds();

        System.out.println(ids);
        for (Pair<String, Long> lr : ids) {
            System.out.println(collectionServerUtils.getCollection(lr.getSecond()));
        }

        var oneDeleting = ids.getLast();
        System.out.println(oneDeleting);
        collectionServerUtils.deleteCollection(oneDeleting.getSecond());
        ids = collectionServerUtils.getAllCollectionNameIds();
        System.out.println(ids);
    }



    /*
    public static void test1() {

    CollectionServerUtils collectionServerUtils = new CollectionServerUtils("http://localhost:8080/");
        collectionServerUtils.addTestCollection();

        var colId = collectionServerUtils.getAllCollectionNameIds().getFirst().getSecond();
        var collection = collectionServerUtils.getCollection(colId);

        collection.addNote(new CollectionNote("Hello", "World"));
        collection.addNote(new CollectionNote("Hello1", "World2"));

        collectionServerUtils.updateCollection(collection);

    }
    

    public Collection updateCollection(Collection collection) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/collections/"+collection.id)
                .request(APPLICATION_JSON)
                .put(Entity.entity(collection, APPLICATION_JSON), Collection.class);

    } */

    public void deleteCollection(long collectionId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/collections/delete/" + collectionId)
                .request(APPLICATION_JSON)
                .delete();
    }


    public Collection getCollection(long collectionId) {
        return ClientBuilder.newClient().target(server)
                .path("api/collections/id/" + collectionId)
                .request(APPLICATION_JSON)
                .get(Collection.class);
    }

    public List<Pair<String, Long>> getAllCollectionNameIds() {
        return ClientBuilder.newClient().target(server)
                .path("api/collections/list")
                .request(APPLICATION_JSON)

                .get(new GenericType<List<Pair<String, Long>>>() {
                });

    }

    public Collection addCollection(Collection collection) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/collections")
                .request(APPLICATION_JSON)
                .post(Entity.entity(collection, APPLICATION_JSON), Collection.class);
    }

    public void addTestCollection() {
        CollectionNote note = new CollectionNote("Hello", "World");

        Collection collection = new Collection("Collection");
        collection.addNote(note);
        System.out.println(collection);

        addCollection(collection);
    }

    /**
     * Retrieves all collections from the server.
     *
     * @return a list of {@link Collection} objects fetched from the server
     */
    public List<Collection> getCollections() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/collections") //
                .request(APPLICATION_JSON) //
                .get(new GenericType<List<Collection>>() {
                });
    }
}
