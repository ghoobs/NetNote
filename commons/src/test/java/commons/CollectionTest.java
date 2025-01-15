package commons;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class CollectionTest {

    private CollectionNote note1;
    private CollectionNote note2;
    private Collection collection1;
    private Collection collection2;

    @BeforeEach
    void setUp() {
        note1 = new CollectionNote(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note2 = new CollectionNote("", "");

        collection1 = new Collection("Test1", List.of(note1, note2));
        collection2 = new Collection("Test1", List.of(note1, note2));
    }


    @Test
    void testToString() {
        String expected = "Test1\nGrocery List\nUnnamed Note\n";
        assertEquals(expected, collection1.toString());
    }

    @Test
    void testEquals() {
        assertEquals(collection1, collection2);
    }

    @Test
    void testHashCode() {
        assertEquals(collection1.hashCode(), collection2.hashCode());
    }
}
