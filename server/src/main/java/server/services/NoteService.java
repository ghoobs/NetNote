package server.services;

import commons.Collection;
import commons.CollectionNote;
import commons.Note;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    /**
     * Instantiates this service
     * @param noteRepository Note repository
     */
    public NoteService(
            NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Gets note by id
     * @param noteId Primary key of the note
     * @return A valid Note if it exists, null otherwise
     */
    public Note getNoteById(long noteId) {
        return noteRepository.findById(noteId).orElse(null);
    }

    /**
     * Saves the provided note to the database
     * @param note A valid non-null Note
     * @return Updated note
     */
    public Note saveNote(Note note) {
        return noteRepository.save(note);
    }

    /**
     * Updates the provided note in the database
     * @param noteId Primary key of the note
     * @return Updated note if note exists, null otherwise
     */
    public Note updateNoteById(long noteId) {
        Note note = getNoteById(noteId);
        if (note == null) {
            return null;
        }
        return noteRepository.save(note);
    }

    /**
     * Erases the provided note from the database
     * @param noteId A valid note id
     */
    public void eraseNoteById(long noteId) {
        noteRepository.deleteById(noteId);
    }

    /**
     * Checks if the provided note exists in the database
     * @param noteId A valid note id
     * @return True if the note exists.
     */
    public boolean doesNoteExist(long noteId) {
        return noteRepository.existsById(noteId);
    }

    /**
     * Gets all the notes in the repository
     * @return Valid list of notes
     */
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    /**
     * Gets all notes pertaining to a specific collection
     * @param collection Collection to search in
     * @return List of notes contained in the collection
     */
    public List<Note> getNotesFromCollection(Collection collection) {
        if (collection == null) {
            //throw new IllegalArgumentException("Collection must not be null!");
            return noteRepository.findAll();
        }
        return noteRepository.findAll()
                .stream()
                .map(note -> (CollectionNote)note)
                .filter(note -> note.collection.id==collection.id)
                .map(note -> (Note)note)
                .toList();
    }

    /**
     * Renames all references [[...]] inside of notes that match the old title to the new title
     * @param oldTitle Old title of the note reference to rename
     * @param newTitle New title of the note reference to set
     * @param collection Collection in which the note is situated
     * @return List of notes that have been modified
     */
    public List<Note> renameAllNoteReferences(String oldTitle, String newTitle, Collection collection) {
        List<Note> allNotes = getNotesFromCollection(collection);
        List<Note> modifiedNotes = new ArrayList<>() ;
        allNotes.forEach(
                (note) -> {
                    String oldText = note.text;
                    note.text = note.text.replace(
                            "[[" + oldTitle + "]]",
                            "[[" + newTitle + "]]"
                    );
                    if (!oldText.equals(note.text)) {
                        modifiedNotes.add(note);
                    }
                }
        );
        return modifiedNotes;
    }

}
