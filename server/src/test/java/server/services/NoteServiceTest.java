package server.services;

import commons.Note;
import commons.CollectionNote;
import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.NoteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNoteById_ExistingId() {
        long noteId = 1L;
        Note fakeNote = new Note("New Note", "Lorem ipsum");
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(fakeNote));

        Note result = noteService.getNoteById(noteId);

        assertNotNull(result);
        assertEquals("New Note", result.title);
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void testGetNoteById_InvalidId() {
        long noteId = 1L;
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        Note result = noteService.getNoteById(noteId);

        assertNull(result);
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void testSaveNote_ValidNote() {
        Note fakeNote = new Note("New Note", "Lorem ipsum");
        when(noteRepository.save(fakeNote)).thenReturn(fakeNote);

        Note result = noteService.saveNote(fakeNote);

        assertNotNull(result);
        assertEquals("New Note", result.title);
        verify(noteRepository, times(1)).save(fakeNote);
    }

    @Test
    void testUpdateNoteById_ValidNote() {
        long noteId = 1L;
        Note existingNote = new Note("Old Title", "Some kind of text...");
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        Note result = noteService.updateNoteById(noteId);

        assertNotNull(result);
        assertEquals("Old Title", result.title);
        verify(noteRepository, times(1)).findById(noteId);
        verify(noteRepository, times(1)).save(existingNote);
    }

    @Test
    void testUpdateNoteById_InvalidNote() {
        long noteId = 1L;
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        Note result = noteService.updateNoteById(noteId);

        assertNull(result);
        verify(noteRepository, times(1)).findById(noteId);
        verify(noteRepository, times(0)).save(any());
    }

    @Test
    void testEraseNoteById_ValidNote() {
        long noteId = 1L;

        noteService.eraseNoteById(noteId);

        verify(noteRepository, times(1)).deleteById(noteId);
    }

    @Test
    void testDoesNoteExist_ValidNote() {
        long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(true);

        boolean result = noteService.doesNoteExist(noteId);

        assertTrue(result);
        verify(noteRepository, times(1)).existsById(noteId);
    }

    @Test
    void testDoesNoteExist_InvalidNote() {
        long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(false);

        boolean result = noteService.doesNoteExist(noteId);

        assertFalse(result);
        verify(noteRepository, times(1)).existsById(noteId);
    }

    @Test
    void testGetAllNotes() {
        List<Note> listNotes = Arrays.asList(
            new Note("Note 1", "bla bla bla"),
            new Note("Note 2", "CSEP is finally over")
        );
        when(noteRepository.findAll()).thenReturn(listNotes);

        List<Note> result = noteService.getAllNotes();

        assertNotNull(result);

        List<Note> expected = Arrays.asList(
            new Note("Note 1", "bla bla bla"),
            new Note("Note 2", "CSEP is finally over")
        );

        assertEquals(expected, result);
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void testGetAllNotesFromCollection() {
        Collection mockCollection = new Collection("Test Collection");
        mockCollection.id = 1;
        Collection mockCollection2 = new Collection("Different Collection");
        mockCollection2.id = 2;

        List<Note> listNotes = Arrays.asList(
            new CollectionNote("Note 1", "bla bla bla", mockCollection),
            new CollectionNote("Note 2", "Lorem Ipsum", mockCollection),
            new CollectionNote("Note 3", "CSEP is finally over", mockCollection2)
        );
        when(noteRepository.findAll()).thenReturn(listNotes);

        List<Note> result = noteService.getNotesFromCollection(mockCollection);
        assertNotNull(result);

        List<Note> expected = Arrays.asList(
            new CollectionNote("Note 1", "bla bla bla", mockCollection),
            new CollectionNote("Note 2", "Lorem Ipsum", mockCollection)
        );

        assertEquals(expected, result);
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void testRenameAllNoteReferences() {
        Collection mockCollection = new Collection("Test Collection");
        mockCollection.id = 1;
        Collection mockCollection2 = new Collection("No Collection");
        mockCollection2.id = 2;

        List<Note> listNotes = Arrays.asList(
            new CollectionNote("Note 1", "bla bla bla", mockCollection),
            new CollectionNote("Note 2", "[[Note 1]]", mockCollection),
            new CollectionNote("Note X", "I love [[Note 1]]", mockCollection),
            new CollectionNote("Note 3", "[[Note 1]]", mockCollection2)
        );
        when(noteRepository.findAll()).thenReturn(listNotes);

        listNotes.get(0).title = "Hello World";
        List<Note> result = noteService.renameAllNoteReferences("Note 1", "Hello World", mockCollection);
        assertNotNull(result);

        List<Note> expected = Arrays.asList(
            new CollectionNote("Note 2", "[[Hello World]]", mockCollection),
            new CollectionNote("Note X", "I love [[Hello World]]", mockCollection)
        );
        assertEquals(result, expected);
        verify(noteRepository, times(1)).findAll();
    }
}
