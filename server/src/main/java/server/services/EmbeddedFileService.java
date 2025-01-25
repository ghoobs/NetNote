package server.services;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.regex.Pattern;
import java.util.ArrayList;
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
        if (optionalNote.isEmpty()) {
            return null;
        }
        Note note = optionalNote.get();
        return note.getEmbeddedFiles();
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

    /**
     * Renames all references ![foo](...) inside of notes that match the old file name to the new file name
     * @param note Note that the embedded file belongs to
     * @param oldFileName old filename to rename
     * @param newFileName new filename to set
     * @return if the note's text has been modified
     */
    public boolean renameAllEmbeddedFileReferences(Note note, String oldFileName, String newFileName) {
        boolean modified = false;
        String pattern = Pattern.quote(oldFileName);//I want to know the right format
        String regExpression = 
            EmbeddedFile.getMarkdownRegex(
                EmbeddedFile.REGEX_ALT_NAMING_FORMAT,
                pattern
            );
        String oldText = note.text;
        note.text = note.text.replaceAll(
                regExpression,
                "![$1]("+newFileName+")"
        );
        if (!oldText.equals(note.text)) {
            modified = true;
            noteRepository.save(note);
        }
        return modified;
    }
}
