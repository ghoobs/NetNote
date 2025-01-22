package server.api;

import commons.Collection;
import commons.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.List;


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
    private final CollectionRepository collections;

    /**
     * Instantiates a new Collection controller.
     *
     * @param collectionsRepo the collection repository
     * @param noteRepo        the note repository
     */
    public CollectionController(CollectionRepository collectionsRepo, NoteRepository noteRepo) {
        this.collections = collectionsRepo;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Collection> delete(@PathVariable("id") Long id) {
        if (!collections.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        collections.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets all Collections in complete form.
     *
     * @return all the collections
     */
    @GetMapping(path = {"", "/"})
    public List<Collection> getAll() {
        return collections.findAll();
    }

    /**
     * Gets a Collection by ID.
     *
     * @param id the id
     * @return the by id
     */
    @GetMapping("id/{id}")
    public ResponseEntity<Collection> getById(@PathVariable("id") long id) {
        if (id < 0 || !collections.existsById(id))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(collections.findById(id).get());
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
        if (collectionAdding.name == null) {
            return ResponseEntity.badRequest().build();
        }
        Collection savedCollection = collections.save(collectionAdding);
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


        if (!collections.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Collection existingCollection = collections.getReferenceById(id);

        existingCollection.name = updatedCollection.name;

        if (updatedCollection.notes != null) {
            existingCollection.updateNotes(updatedCollection.notes);
        }


        return ResponseEntity.ok(collections.save(existingCollection));
    }
}
