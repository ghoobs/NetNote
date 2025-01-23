package server.api;

import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.services.CollectionService;
import server.services.NoteService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionControllerTest {

    private TestNoteRepository testNoteRepository;
    private TestCollectionRepository testCollectionRepository;
    private NoteService testNoteService;
    private CollectionService testCollectionService;
    private CollectionController collectionController;
    private Collection c1;
    private Collection c2;

    @BeforeEach
    void setUp() {
        testNoteRepository = new TestNoteRepository();
        testCollectionRepository = new TestCollectionRepository();
        testNoteService = new NoteService(testNoteRepository);
        testCollectionService = new CollectionService(testCollectionRepository);
        collectionController = new CollectionController(testCollectionService, testNoteService);

        c1 = new Collection("Test Collection");
        c2 = new Collection("Test Collection 2");
        collectionController.addCollection(c1);
        collectionController.addCollection(c2);
    }

    @Test
    void getAll() {
        assertEquals(List.of("Test Collection", "Test Collection 2"), collectionController.getAll().stream().map(x -> x.name).toList());
    }

    @Test
    void getById() {
        var id = c1.id;
        assertEquals(c1, collectionController.getById(id).getBody());
    }

    @Test
    void addCollection(){
        Collection c3 = new Collection("Test Collection 3");
        collectionController.addCollection(c3);

        assertTrue(collectionController.getAll().contains(c3));
    }

    @Test
    void deleteCollection(){
        collectionController.delete(c1.id);

        assertFalse(collectionController.getAll().contains(c1));
    }

    @Test
    void updateCollection() {
        Collection collection = new Collection("test");
        collectionController.addCollection(collection);

        Collection updatedCollection = new Collection("test but updated");
        ResponseEntity<Collection> responseEntity = collectionController.updateCollection(collection.id, updatedCollection);

        assertNotNull(responseEntity.getBody());
        assertEquals("test but updated", responseEntity.getBody().name);
    }

}