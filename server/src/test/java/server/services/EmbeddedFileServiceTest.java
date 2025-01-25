package server.services;

import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmbeddedFileServiceTest {

    private NoteRepository noteRepository;
    private EmbeddedFileService embeddedFileService;

    @BeforeEach
    void setUp() {
        noteRepository = mock(NoteRepository.class);
        embeddedFileService = new EmbeddedFileService(noteRepository);
    }

    @Test
    void getAllEmbeddedFilesFromNote_NoteExists_ReturnsEmbeddedFiles() {
        long noteId = 1L;
        List<EmbeddedFile> embeddedFiles = new ArrayList<>();
        embeddedFiles.add(new EmbeddedFile("file1.txt", "text/plain", null));
        embeddedFiles.add(new EmbeddedFile("file2.txt", "text/plain", null));

        Note note = new Note();
        note.setEmbeddedFiles(embeddedFiles);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        List<EmbeddedFile> result = embeddedFileService.getAllEmbeddedFilesFromNote(noteId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getFilename());
        assertEquals("file2.txt", result.get(1).getFilename());
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void getAllEmbeddedFilesFromNote_NoteDoesNotExist_ReturnsNull() {
        long noteId = 1L;
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        List<EmbeddedFile> result = embeddedFileService.getAllEmbeddedFilesFromNote(noteId);

        assertNull(result);
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void getEmbeddedFileFromNote_FileExists_ReturnsFile() {
        long noteId = 1L;
        String filename = "file1.txt";

        List<EmbeddedFile> embeddedFiles = new ArrayList<>();
        embeddedFiles.add(new EmbeddedFile("file1.txt", "text/plain", null));
        embeddedFiles.add(new EmbeddedFile("file2.txt", "text/plain", null));

        Note note = new Note();
        note.setEmbeddedFiles(embeddedFiles);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        EmbeddedFile result = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);

        assertNotNull(result);
        assertEquals("file1.txt", result.getFilename());
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void getEmbeddedFileFromNote_FileDoesNotExist_ReturnsNull() {
        long noteId = 1L;
        String filename = "nonexistent.txt";

        List<EmbeddedFile> embeddedFiles = new ArrayList<>();
        embeddedFiles.add(new EmbeddedFile("file1.txt", "text/plain", null));
        embeddedFiles.add(new EmbeddedFile("file2.txt", "text/plain", null));

        Note note = new Note();
        note.setEmbeddedFiles(embeddedFiles);

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        EmbeddedFile result = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);

        assertNull(result);
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void getEmbeddedFileFromNote_NoteDoesNotExist_ReturnsNull() {
        long noteId = 1L;
        String filename = "file1.txt";
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        EmbeddedFile result = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);

        assertNull(result);
        verify(noteRepository, times(1)).findById(noteId);
    }
}
