package server.services;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmbeddedFileService {
    private final NoteRepository noteRepository;

    /**
     * Initialises this service with a note repository
     * @param noteRepository Note repository
     */
    public EmbeddedFileService(NoteRepository noteRepository) {
        this.noteRepository=noteRepository;
    }

    /**
     * Gets list of embedded files
     * @param noteId id of the note
     * @return List of embeds. Will be null if the note doesn't exist
     */
    public List<EmbeddedFile> getAllEmbeddedFilesFromNote(long noteId) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        return optionalNote.map(Note::getEmbeddedFiles).orElse(null);
    }

    /**
     * Gets specific embedded file by name
     * @param noteId id of the note
     * @param filename name of the file
     * @return Embedded file. Will be null if the note doesn't exist, or if the file doesn't exist.
     */
    public EmbeddedFile getEmbeddedFileFromNote(long noteId, String filename) {
        List<EmbeddedFile> files = getAllEmbeddedFilesFromNote(noteId);
        if (files == null) {
            return null;
        }
        return files.stream()
                .filter(that -> that.getFilename().equals(filename))
                .findFirst()
                .orElse(null);
    }
}
