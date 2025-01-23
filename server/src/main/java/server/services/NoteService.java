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
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
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
        List<Note> modifiedNotes = new ArrayList<Note>() ;
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
