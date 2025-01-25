package server.services;

import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.CollectionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionService collectionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCollectionById_ExistingId_ReturnsCollection() {
        long collectionId = 1L;
        Collection mockCollection = new Collection("Test Collection");
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(mockCollection));

        Collection result = collectionService.getCollectionById(collectionId);

        assertNotNull(result);
        assertEquals("Test Collection", result.name);
        verify(collectionRepository, times(1)).findById(collectionId);
    }

    @Test
    void getCollectionById_NonExistingId_ReturnsNull() {
        long collectionId = 1L;
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

        Collection result = collectionService.getCollectionById(collectionId);

        assertNull(result);
        verify(collectionRepository, times(1)).findById(collectionId);
    }

    @Test
    void saveCollection_ValidCollection_ReturnsSavedCollection() {
        Collection mockCollection = new Collection("New Collection");
        when(collectionRepository.save(mockCollection)).thenReturn(mockCollection);

        Collection result = collectionService.saveCollection(mockCollection);

        assertNotNull(result);
        assertEquals("New Collection", result.name);
        verify(collectionRepository, times(1)).save(mockCollection);
    }

    @Test
    void updateCollectionById_ExistingCollection_ReturnsUpdatedCollection() {
        long collectionId = 1L;
        Collection existingCollection = new Collection("Old Collection");
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(existingCollection));
        when(collectionRepository.save(existingCollection)).thenReturn(existingCollection);

        Collection result = collectionService.updateCollectionById(collectionId);

        assertNotNull(result);
        assertEquals("Old Collection", result.name);
        verify(collectionRepository, times(1)).findById(collectionId);
        verify(collectionRepository, times(1)).save(existingCollection);
    }

    @Test
    void updateCollectionById_NonExistingCollection_ReturnsNull() {
        long collectionId = 1L;
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

        Collection result = collectionService.updateCollectionById(collectionId);

        assertNull(result);
        verify(collectionRepository, times(1)).findById(collectionId);
        verify(collectionRepository, times(0)).save(any());
    }

    @Test
    void eraseCollectionById_ValidId_CallsDeleteById() {
        long collectionId = 1L;

        collectionService.eraseCollectionById(collectionId);

        verify(collectionRepository, times(1)).deleteById(collectionId);
    }

    @Test
    void doesCollectionExist_ExistingId_ReturnsTrue() {
        long collectionId = 1L;
        when(collectionRepository.existsById(collectionId)).thenReturn(true);

        boolean result = collectionService.doesCollectionExist(collectionId);

        assertTrue(result);
        verify(collectionRepository, times(1)).existsById(collectionId);
    }

    @Test
    void doesCollectionExist_NonExistingId_ReturnsFalse() {
        long collectionId = 1L;
        when(collectionRepository.existsById(collectionId)).thenReturn(false);

        boolean result = collectionService.doesCollectionExist(collectionId);

        assertFalse(result);
        verify(collectionRepository, times(1)).existsById(collectionId);
    }

    @Test
    void getAllCollections_ReturnsListOfCollections() {
        List<Collection> mockCollections = Arrays.asList(
                new Collection("Collection 1"),
                new Collection("Collection 2")
        );
        when(collectionRepository.findAll()).thenReturn(mockCollections);

        List<Collection> result = collectionService.getAllCollections();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(collectionRepository, times(1)).findAll();
    }

    @Test
    void collectionById_ExistingId_ReturnsOptionalCollection() {
        long collectionId = 1L;
        Collection mockCollection = new Collection("Test Collection");
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(mockCollection));

        Optional<Collection> result = collectionService.collectionById(collectionId);

        assertTrue(result.isPresent());
        assertEquals("Test Collection", result.get().name);
        verify(collectionRepository, times(1)).findById(collectionId);
    }

    @Test
    void collectionById_NonExistingId_ReturnsEmptyOptional() {
        long collectionId = 1L;
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.empty());

        Optional<Collection> result = collectionService.collectionById(collectionId);

        assertFalse(result.isPresent());
        verify(collectionRepository, times(1)).findById(collectionId);
    }
}
