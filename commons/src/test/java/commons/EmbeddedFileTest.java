package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmbeddedFileTest {

    @Test
    public void testNoArgsConstructor() {
        EmbeddedFile file = new EmbeddedFile();

        assertNotNull(file);
        assertEquals(0, file.getId());
        assertNull(file.getFilename());
        assertNull(file.getFiletype());
        assertNull(file.getUrl());
        assertNull(file.getData());
        assertNull(file.getNote());
    }

    @Test
    public void testAllArgsConstructor() {
        byte[] data = {1, 2, 3};
        Note note = new Note();
        EmbeddedFile file = new EmbeddedFile("test.txt", "test/plain",
                "http://test.com", 42L, data, note);

        assertEquals("test.txt", file.getFilename());
        assertEquals("test/plain", file.getFiletype());
        assertEquals("http://test.com", file.getUrl());
        assertEquals(42L, file.getId());
        assertArrayEquals(data, file.getData());
        assertEquals(note, file.getNote());
    }

    @Test
    public void testGettersAndSetters() {
        EmbeddedFile file = new EmbeddedFile();
        byte[] data = {4, 5, 6};
        Note note = new Note();

        file.setId(1L);
        file.setFilename("test.txt");
        file.setFiletype("test/plain");
        file.setUrl("http://test.com/file");
        file.setData(data);
        file.setNote(note);

        assertEquals(1L, file.getId());
        assertEquals("test.txt", file.getFilename());
        assertEquals("test/plain", file.getFiletype());
        assertEquals("http://test.com/file", file.getUrl());
        assertArrayEquals(data, file.getData());
        assertEquals(note, file.getNote());
    }

    @Test
    public void testEqualsAndHashCode() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 3};
        Note note = new Note();

        EmbeddedFile file1 = new EmbeddedFile("file1.txt",
                "test/plain", "url1", 100L, data1, note);
        file1.setId(1L);
        EmbeddedFile file2 = new EmbeddedFile("file1.txt",
                "test/plain", "url1", 100L, data2, note);
        file2.setId(1L);
        EmbeddedFile file3 = new EmbeddedFile("file3.txt",
                "test/html", "url2", 101L, new byte[]{4, 5, 6}, note);
        file3.setId(2L);

        assertEquals(file1, file2);
        assertNotEquals(file1, file3);
        assertEquals(file1.hashCode(), file2.hashCode());
        assertNotEquals(file1.hashCode(), file3.hashCode());
        assertNotEquals(file2.hashCode(), file3.hashCode());
    }

    @Test
    public void testEqualsWithNullAndDifferentClass() {
        EmbeddedFile file = new EmbeddedFile();
        file.setId(1L);
        assertNotEquals(null, file);
        assertNotEquals("String here", file);
    }

    @Test
    public void testHashCodeConsistency() {
        EmbeddedFile file = new EmbeddedFile("test.txt",
                "test", "url", 1L, new byte[]{1, 2, 3}, new Note());
        file.setId(1L);
        int hashCode1 = file.hashCode();
        int hashCode2 = file.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testEqualsSameObject() {
        EmbeddedFile file = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file.setId(1L);
        assertEquals(file, file);
    }

    @Test
    public void testEqualsSameValues() {
        EmbeddedFile file1 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file1.setId(1L);

        EmbeddedFile file2 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file2.setId(1L);

        assertEquals(file1, file2);
        assertEquals(file2, file1);
    }

    @Test
    public void testEqualsDifferentValues() {
        EmbeddedFile file1 = new EmbeddedFile("test1.txt",
                "test/plain", "http://test.com/1", 1L, new byte[]{1, 2, 3}, new Note());
        file1.setId(1L);

        EmbeddedFile file2 = new EmbeddedFile("test2.txt",
                "application/json", "http://test.com/2", 2L, new byte[]{4, 5, 6}, new Note());
        file2.setId(2L);

        assertNotEquals(file1, file2);
        assertNotEquals(file2, file1);
    }

    @Test
    public void testEqualsNull() {
        EmbeddedFile file = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file.setId(1L);

        assertNotEquals(null, file);
    }

    @Test
    public void testEqualsDifferentClass() {
        EmbeddedFile file = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file.setId(1L);

        String differentClass = "String here";

        assertNotEquals(differentClass, file);
    }

    @Test
    public void testEqualsConsistency() {
        EmbeddedFile file1 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file1.setId(1L);

        EmbeddedFile file2 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file2.setId(1L);

        for (int i = 0; i < 10; i++) {
            assertEquals(file1, file2);
        }
    }

    @Test
    public void testEqualsTransitivity() {
        EmbeddedFile file1 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file1.setId(1L);

        EmbeddedFile file2 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file2.setId(1L);

        EmbeddedFile file3 = new EmbeddedFile("test.txt",
                "test/plain", "http://test.com", 1L, new byte[]{1, 2, 3}, new Note());
        file3.setId(1L);

        assertEquals(file1, file2);
        assertEquals(file2, file3);
        assertEquals(file1, file3);
    }
}