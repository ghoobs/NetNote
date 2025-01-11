package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {
    @Test
    void testToString() {
        Tag tag1 = new Tag("Sunny");
        assertEquals("Sunny", tag1.toString());
    }

    @Test
    void testEquals() {
        Tag tag1 = new Tag("Sunny");
        Tag tag2 = new Tag("Sunny");

        assertEquals(tag1, tag2);
    }

    @Test
    void testHashCode() {
        Tag tag1 = new Tag("Sunny");
        Tag tag2 = new Tag("Sunny");

        assertEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    void testValidityIsValid(){
        Tag tag = new Tag("a");
        assertTrue(tag.valid());
    }

    @Test
    void testValidityIsInvalidEmptyString(){
        Tag tag = new Tag("");
        assertFalse(tag.valid());
    }

    @Test
    void testValidityIsInvalidNullString(){
        Tag tag = new Tag();
        assertFalse(tag.valid());
    }
}
