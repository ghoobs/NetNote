package server.api;

import commons.Collection;
import commons.CollectionNote;
import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedFileControllerTest {

    private EmbeddedFileController embeddedFileController;
    private TestNoteRepository testNoteRepository;
    private TestCollectionRepository testCollectionRepository;
    private Collection testCollection;
    private CollectionNote testNote;
    private EmbeddedFile testEmbeddedFile;

    @BeforeEach
    void setUp() {
        testNoteRepository = new TestNoteRepository();
        testCollectionRepository = new TestCollectionRepository();
        embeddedFileController = new EmbeddedFileController(testNoteRepository, testCollectionRepository);
        testCollection = new Collection("TestCollection");
        testNote = new CollectionNote("TestNote", "Some String");
        testEmbeddedFile = new EmbeddedFile("TestFile", "TestFiletype",
                "TestURL", "data".getBytes(), testNote);
        List<Note> testNoteList = new ArrayList<>();
        testNoteList.add(testNote);
        testCollection.notes.add(testNote);
        testNote.addEmbeddedFile(testEmbeddedFile);
        testCollection = testCollectionRepository.save(testCollection);
        testNote = testNoteRepository.save(testNote);
    }

    @Test
    void addFile() {
        EmbeddedFile testFile2 = new EmbeddedFile("TestFile2", "TestFiletype2",
                "TestURL2", "data2".getBytes(), null);
        EmbeddedFile result = embeddedFileController.addFile(testCollection.id,
                testNote.getTitle(), testFile2).getBody();
        assertNotNull(result);
        assertEquals(testFile2, result);
    }

    @Test
    void getData() {
        byte[] data = embeddedFileController.getData(testCollection.id, testNote.getTitle(),
                testEmbeddedFile.getFilename()).getBody();
        assertNotNull(data);
        assertArrayEquals(testEmbeddedFile.getData(), data);
    }

    @Test
    void deleteFile() {
        ResponseEntity<Note> response = embeddedFileController.deleteFile(testCollection.id,
                testNote.getTitle(), testEmbeddedFile.getFilename());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getEmbeddedFiles().isEmpty());
    }

    @Test
    void renameFile() {
        String newFilename = "RenamedTestFile";
        EmbeddedFile renamedFile = embeddedFileController.renameFile(testCollection.id,
                testNote.getTitle(), testEmbeddedFile.getFilename(), newFilename).getBody();
        assertNotNull(renamedFile);
        assertEquals(newFilename, renamedFile.getFilename());
    }

}