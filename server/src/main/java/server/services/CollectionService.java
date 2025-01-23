package server.services;

import org.springframework.stereotype.Service;
import server.database.CollectionRepository;

import commons.Collection;

import java.util.List;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;

    /**
     * Initialises this service
     * @param collectionRepository Collection repository
     */
    public CollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * Gets collection by id
     * @param collectionId Primary key of the collection
     * @return A valid collection if it exists, null otherwise
     */
    public Collection getCollectionById(long collectionId) {
        return collectionRepository.findById(collectionId).orElse(null);
    }

    /**
     * Saves the provided collection to the database
     * @param collection A valid non-null Collection
     * @return Updated collection
     */
    public Collection saveCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    /**
     * Updates the provided collection in the database
     * @param collectionId Primary key of the collection
     * @return Updated collection if note exists, null otherwise
     */
    public Collection updateCollectionById(long collectionId) {
        Collection collection = getCollectionById(collectionId);
        if (collection == null) {
            return null;
        }
        return collectionRepository.save(collection);
    }

    /**
     * Erases the provided collection from the database
     * @param collectionId A valid collection id
     */
    public void eraseCollectionById(long collectionId) {
        collectionRepository.deleteById(collectionId);
    }

    /**
     * Checks if the provided collection exists in the database
     * @param collectionId A valid collection id
     * @return True if the collection exists.
     */
    public boolean doesCollectionExist(long collectionId) {
        return collectionRepository.existsById(collectionId);
    }

    /**
     * Gets all the collections in the repository
     * @return Valid list of collections
     */
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }
}
