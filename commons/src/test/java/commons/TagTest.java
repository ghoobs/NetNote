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
    @Test
    void testDefaultConstructor() {
        Tag tag = new Tag();
        assertEquals("", tag.name, "Default constructor should initialize name to an empty string.");
    }

    @Test
    void testParameterizedConstructor() {
        Tag tag = new Tag("example");
        assertEquals("example", tag.name, "Parameterized constructor should initialize name with the given value.");
    }
    @Test
    void testEquals_SameObject() {
        Tag tag = new Tag("example");
        assertTrue(tag.equals(tag), "A tag should be equal to itself.");
    }
    @Test
    void testEquals_EqualObjects() {
        Tag tag1 = new Tag("example");
        Tag tag2 = new Tag("example");
        assertTrue(tag1.equals(tag2), "Tags with the same name should be equal.");
    }
    @Test
    void testEquals_DifferentObjects() {
        Tag tag1 = new Tag("example");
        Tag tag2 = new Tag("different");
        assertFalse(tag1.equals(tag2), "Tags with different names should not be equal.");
    }
    @Test
    void testEquals_Null() {
        Tag tag = new Tag("example");
        assertFalse(tag.equals(null), "A tag should not be equal to null.");
    }
    @Test
    void testEquals_DifferentClass() {
        Tag tag = new Tag("example");
        String other = "example";
        assertFalse(tag.equals(other), "A tag should not be equal to an object of a different class.");
    }
    @Test
    void testValid_EmptyTag() {
        Tag tag = new Tag();
        assertFalse(tag.valid(), "A tag with an empty name should not be valid.");
    }
    @Test
    void testValid_NullTag() {
        Tag tag = new Tag(null);
        assertFalse(tag.valid(), "A tag with a null name should not be valid.");
    }
    @Test
    void testValid_ValidTag() {
        Tag tag = new Tag("example");
        assertTrue(tag.valid(), "A tag with a non-empty, non-null name should be valid.");
    }
}
