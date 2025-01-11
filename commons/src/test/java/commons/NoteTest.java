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

    @Test
    void testKeywordEfficacyOnlyTags() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note.tags.add(new Tag("Saturday"));
        note.tags.add(new Tag("Food"));
        note.tags.add(new Tag("Shopping"));
        assertTrue(note.hasKeyword("Saturday"));
        assertTrue(note.hasKeyword("FOod"));
        assertTrue(note.hasKeyword("shOpping"));
        assertFalse(note.hasKeyword("Chocolate"));
    }

    @Test
    void testKeywordEfficacyOnlyTitle() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        assertTrue(note.hasKeyword("grocery"));
        assertTrue(note.hasKeyword("LIST"));
        assertFalse(note.hasKeyword("What"));
    }

    @Test
    void testKeywordEfficacyOnlyBody() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        assertTrue(note.hasKeyword("Milk"));
        assertTrue(note.hasKeyword("BANANAs"));
        assertTrue(note.hasKeyword("utter"));
        assertFalse(note.hasKeyword("?"));
    }

    @Test
    void testKeywordEfficacyInvertedSearch() {
        Note note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note.tags.add(new Tag("Saturday"));
        note.tags.add(new Tag("Food"));
        note.tags.add(new Tag("Shopping"));

        assertTrue(note.hasKeyword("Grocery list Items"));
        assertTrue(note.hasKeyword("Saturday morning food shopping"));
        assertTrue(note.hasKeyword("chocolate food"));
        assertFalse(note.hasKeyword("chocolate cake"));
    }
}
