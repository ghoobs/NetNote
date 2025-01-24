package server.api;

import commons.Collection;
import commons.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CollectionService;
import server.services.NoteService;

import java.util.List;
import java.util.Optional;


/**
 * A basic Rest Controller for Collections
 * <p>
 * Useful Mappings:
 * /api/collections : gets all Collections
 * <p>
 * /api/collections/id/{id} : gets the Collection with unique ID: id
 * <p>
 * /api/collections/list : returns all collection names with their IDs
 */
@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private final CollectionService collectionService;
    private final NoteService noteService;

    /**
     * Instantiates a new Collection controller.
     *
     * @param collectionService the collection service
     * @param noteService       the note service
     */
    public CollectionController(
            CollectionService collectionService,
            NoteService noteService) {
        this.collectionService = collectionService;
        this.noteService = noteService;

        if (collectionService.getAllCollections().isEmpty()){
            Collection collection = new Collection("Standard Collection");
            collectionService.saveCollection(collection);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Collection> delete(@PathVariable("id") Long id) {
        if (!collectionService.doesCollectionExist(id)) {
            return ResponseEntity.notFound().build();
        }

        collectionService.eraseCollectionById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets all Collections in complete form.
     *
     * @return all the collections
     */
    @GetMapping(path = {"", "/"})
    public List<Collection> getAll() {
        return collectionService.getAllCollections();
    }

    /**
     * Gets a Collection by ID.
     *
     * @param id the id
     * @return the by id
     */
    @GetMapping("id/{id}")
    public ResponseEntity<Collection> getById(@PathVariable("id") long id) {
        Collection collection = collectionService.getCollectionById(id);
        if (id < 0 || collection == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(collection);
    }

    /**
     * Get a list of all pairs of titles + ids.
     *
     * @return the list
     */
    @GetMapping("/list")
    public List<Pair<String, Long>> getAllNamesIds() {
        return getAll().stream()
                .map(collection -> new Pair<>(collection.name, collection.id))
                .toList();

    }

    /**
     * Add a Collection to the database.
     *
     * @param collectionAdding the collection adding
     * @return the response entity
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Collection> addCollection(@RequestBody Collection collectionAdding) {
        if (collectionAdding == null || collectionAdding.name == null) {
            return ResponseEntity.badRequest().build();
        }
        Collection savedCollection = collectionService.saveCollection(collectionAdding);
        return ResponseEntity.ok(savedCollection);
    }

    /**
     * Updates an existing collection in the database.
     *
     * @param id                the ID of the collection to be updated
     * @param updatedCollection the {@link Collection} object containing the updated title
     * @return a ResponseEntity containing the updated object if successful,
     * or a ResponseEntity with a bad request status if the collection does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(
            @PathVariable("id") long id,
            @RequestBody Collection updatedCollection
    ) {
        Optional<Collection> existingCollectionOpt = collectionService.collectionById(id);
        if (existingCollectionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var existingCollection = existingCollectionOpt.get();

        existingCollection.name = updatedCollection.name;


        if (updatedCollection.notes != null) {
            existingCollection.updateNotes(updatedCollection.notes);
        }

        return ResponseEntity.ok(collectionService.saveCollection(existingCollection));
    }
}
