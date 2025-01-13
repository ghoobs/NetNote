package commons;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CollectionTest {

    private Note note1;
    private Note note2;
    private Note note3;
    private Collection collection1;
    private Collection collection2;

    @BeforeEach
    void setUp() {
        note1 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note2 = new Note("To-Do", "- Study\n- Finish project");
        note3 = new Note("", ""); // Empty note

        collection1 = new Collection("Test1", new ArrayList<>(List.of(note1, note2)));
        collection2 = new Collection("Test1", new ArrayList<>(List.of(note1, note2)));
    }

    @Test
    void testToString() {
        String expected = "Test1\nGrocery List\nTo-Do\n";
        assertEquals(expected, collection1.toString(), "toString should format the collection correctly.");
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(collection1, collection1, "A collection should be equal to itself.");
    }

    @Test
    void testEquals_EqualObjects() {
        assertEquals(collection1, collection2, "Two collections with the same name and notes should be equal.");
    }

    @Test
    void testEquals_DifferentName() {
        collection2.name="Different Name";
        assertNotEquals(collection1, collection2, "Collections with different names should not be equal.");
    }

    @Test
    void testEquals_DifferentNotes() {
        collection2.notes=new ArrayList<>(List.of(note1)); // Different set of notes
        assertNotEquals(collection1, collection2, "Collections with different notes should not be equal.");
    }

    @Test
    void testEquals_Null() {
        assertNotEquals(collection1, null, "A collection should not be equal to null.");
    }

    @Test
    void testEquals_DifferentClass() {
        assertNotEquals(collection1, new Object(), "A collection should not be equal to an object of a different class.");
    }

    @Test
    void testHashCode_EqualObjects() {
        assertEquals(collection1.hashCode(), collection2.hashCode(), "Equal collections should have the same hash code.");
    }

    @Test
    void testHashCode_DifferentObjects() {
        collection2.name="Different Name";
        assertNotEquals(collection1.hashCode(), collection2.hashCode(), "Collections with different names should have different hash codes.");
    }

    @Test
    void testAddNote() {
        collection1.notes.add(note3);
        assertTrue(collection1.notes.contains(note3), "The note should be added to the collection.");
        assertEquals(3, collection1.notes.size(), "The collection should now contain three notes.");
    }

    @Test
    void testRemoveNote() {
        collection1.notes.remove(note1);
        assertFalse(collection1.notes.contains(note1), "The note should be removed from the collection.");
        assertEquals(1, collection1.notes.size(), "The collection should now contain one note.");
    }

    @Test
    void testClearNotes() {
        collection1.notes.clear();
        assertTrue(collection1.notes.isEmpty(), "The collection should contain no notes after clearing.");
    }

    @Test
    void testEmptyCollectionToString() {
        Collection emptyCollection = new Collection("EmptyCollection", new ArrayList<>());
        assertEquals("EmptyCollection\n", emptyCollection.toString(), "toString should handle an empty collection gracefully.");
    }

    @Test
    void testEmptyCollectionEquals() {
        Collection emptyCollection1 = new Collection("EmptyCollection", new ArrayList<>());
        Collection emptyCollection2 = new Collection("EmptyCollection", new ArrayList<>());
        assertEquals(emptyCollection1, emptyCollection2, "Empty collections with the same name should be equal.");
    }

    @Test
    void testEmptyCollectionHashCode() {
        Collection emptyCollection = new Collection("EmptyCollection", new ArrayList<>());
        assertNotNull(emptyCollection.hashCode(), "Hash code of an empty collection should not throw an exception.");
    }
}
