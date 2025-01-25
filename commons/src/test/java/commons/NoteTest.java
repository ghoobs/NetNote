package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoteTest {

    Note note;
    Note note2;
    Note note3;

    @BeforeEach
    void setUp(){
        note = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note2 = new Note(
                "Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
        note3 = new Note(
                "Different Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter"""
        );
    }

    @Test
    void constructorTest(){
        Note test = new Note("Test", "Test");
        assertNotNull(test);
    }

    @Test
    void emptyConstructorTest(){
        Note test = new Note();
        assertNull(test.title);
        assertNull(test.text);
    }

    @Test
    void testToString() {
        assertEquals("Grocery List\n- Milk...", note.toString());
    }

    @Test
    void testEqualsTrue() {
        assertEquals(note, note2);
    }

    @Test
    void testEqualsFalse() {
        assertNotEquals(note, note3);
    }

    @Test
    void testEqualsWithTags() {
        note.tags.add(new Tag("Saturday"));
        note2.tags.add(new Tag("Saturday"));

        assertEquals(note, note2);
    }

    @Test
    void testNotEqualsWithTags() {
        note.tags.add(new Tag("Saturday"));

        assertNotEquals(note, note2);
    }

    @Test
    void testNotEqualsSameTags(){
        note.tags.add(new Tag("Saturday"));
        note3.tags.add(new Tag("Saturday"));

        assertNotEquals(note, note3);
    }

    @Test
    void testHashCodeTrue() {
        assertEquals(note.hashCode(), note2.hashCode());
    }

    @Test
    void testHashCodeFalse() {
        assertNotEquals(note.hashCode(), note3.hashCode());
    }

    @Test
    void testKeywordEfficacyOnlyTags() {
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
        assertTrue(note.hasKeyword("grocery"));
        assertTrue(note.hasKeyword("LIST"));
        assertFalse(note.hasKeyword("What"));
    }

    @Test
    void testKeywordEfficacyOnlyBody() {
        assertTrue(note.hasKeyword("Milk"));
        assertTrue(note.hasKeyword("BANANAs"));
        assertTrue(note.hasKeyword("utter"));
        assertFalse(note.hasKeyword("?"));
    }

    @Test
    void testKeywordEfficacyInvertedSearch() {
        note.tags.add(new Tag("Saturday"));
        note.tags.add(new Tag("Food"));
        note.tags.add(new Tag("Shopping"));

        assertTrue(note.hasKeyword("Grocery list Items"));
        assertTrue(note.hasKeyword("Saturday morning food shopping"));
        assertTrue(note.hasKeyword("chocolate food"));
        assertFalse(note.hasKeyword("chocolate cake"));
    }

    @Test
    void copyToTest() {
        Note newNote = new Note();
        assertNotEquals(newNote, note);

        note.copyTo(newNote);
        assertEquals(note, newNote);
        assertEquals(note2, newNote);
    }
}
