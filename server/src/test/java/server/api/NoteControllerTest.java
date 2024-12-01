package server.api;

import commons.Note;
import commons.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NoteControllerTest {

    private TestNoteRepository testNoteRepository;

    private NoteController noteController;

    @BeforeEach
    void setUp() {
        testNoteRepository = new TestNoteRepository();
        noteController = new NoteController(testNoteRepository);
    }

    void putData(){
        noteController.addNote(new Note("Title", "Contents"));
    }

    @Test
    void getAllBasic() {
        noteController.addNote(new Note("Title", "Contents"));
        assertEquals(noteController.getAll(), List.of(new Note("Title", "Contents")));
    }

    @Test
    void getAllAdvanced() {
        noteController.addNote(new Note("Title", "Contents"));
        noteController.addNote(new Note("Title2", "Contents2"));
        assertTrue(noteController.getAll().stream().map(x -> x.title).anyMatch(x -> x.equals("Title")));

        assertTrue(noteController.getAll().stream().map(x -> x.title).anyMatch(x -> x.equals("Title2")));
    }
    @Test
    void getById() {
        Note note = new Note("Title", "Contents");
        noteController.addNote(note);
        var id = note.id;
        System.out.println(note.id);
        System.out.println(noteController.getAll().stream().map(x->x.id).collect(Collectors.toList()));
        assertEquals(noteController.getById(id).getBody(), note);
    }

    @Test
    void getAllNamesIds(){
        Note note = new Note("Title", "Contents");
        Pair<String, Long> pair = new Pair<>(note.title, note.id);
        noteController.addNote(note);

        assertTrue(noteController.getAllNamesIds().contains(pair));
    }

    @Test
    void getAllNames() {
        noteController.addNote(new Note("Title", "Contents"));
        noteController.addNote(new Note("Title2", "Contents2"));
        noteController.addNote(new Note("Title3", "Contents3"));
        assertTrue(noteController.getAllNames().contains("Title"));
        assertTrue(noteController.getAllNames().contains("Title2"));
        assertTrue(noteController.getAllNames().contains("Title3"));
    }

    @Test
    void getAllIds() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note("Title1", "Contents1"));
        notes.add(new Note("Title2", "Contents2"));
        notes.add(new Note("Title3", "Contents3"));
        for (Note note : notes) {
            noteController.addNote(note);
        }
        Set<Long> ids = notes.stream().map(x->x.id).collect(Collectors.toSet());
        assertEquals(new HashSet<>(noteController.getAllIds()), ids);
    }

    @Test
    void addNote() {
        Note note = new Note("Title", "Contents");
        Note note2 = new Note("Title2", "Contents2");
        noteController.addNote(note);
        noteController.addNote(note2);
        assertTrue(noteController.getAll().contains(note));
        assertTrue(noteController.getAll().contains(note2));
    }

    @Test
    void addNull(){
        assertEquals(noteController.addNote(new Note(null,null)), ResponseEntity.badRequest().build());
    }

    @Test
    void getNotAvailable(){
        assertEquals(noteController.getById(1), ResponseEntity.notFound().build());
    }

    @Test
    void searchNotesContents() {
        Note note1 = new Note("Title1", "1");
        Note note2 = new Note("Title2", "2");
        Note note3 = new Note("Title3", "3");
        noteController.addNote(note1);
        noteController.addNote(note2);
        noteController.addNote(note3);

        List<Note> searchedNotes = noteController.searchNotes("2");
        assertEquals(1, searchedNotes.size());
        assertTrue(searchedNotes.contains(note2));
    }

    @Test
    void searchNotesTitle() {
        Note note1 = new Note("Title", "1");
        Note note2 = new Note("Title", "2");
        noteController.addNote(note1);
        noteController.addNote(note2);

        List<Note> searchedNotes = noteController.searchNotes("Title");
        assertEquals(2, searchedNotes.size());
        assertTrue(searchedNotes.contains(note1) && searchedNotes.contains(note2));
    }
}