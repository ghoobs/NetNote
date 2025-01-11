package commons;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NoteTest {
    @Test
    void testToString() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );

        assertEquals("Grocery List\n- Milk...", note.toString());
    }

    @Test
    void testEquals() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        Note note2 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );

        assertEquals(note, note2);
    }

    @Test
    void testEqualsWithTags() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note.tags.add(new Tag("Saturday"));
        Note note2 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note2.tags.add(new Tag("Saturday"));

        assertEquals(note, note2);
    }
    @Test
    void testNotEqualsWithTags() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note.tags.add(new Tag("Saturday"));
        Note note2 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );

        assertNotEquals(note, note2);
    }


    @Test
    void testHashCode() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        Note note2 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );

        assertEquals(note.hashCode(), note2.hashCode());
    }
}
