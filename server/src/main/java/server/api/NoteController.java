package server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import commons.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Note;


import server.database.NoteRepository;

/**
 * A basic Rest Controller for Notes
 * <p>
 * Useful Mappings:
 * /api/notes : gets all Notes
 * <p>
 * /api/notes/id/{id} : gets the Note with unique ID: id
 * <p>
 * /api/notes/list : #Unimplemented returns all note names with their IDs
 * <p>
 * Optional until we have agreed on the list ->
 * /api/notes/names : List of names of notes
 * /api/notes/ids : List of ids of notes
 * /api/notes/search
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository notes;

    /**
     * Instantiates a new Note controller.
     *
     * @param repo the repo
     */
    public NoteController(NoteRepository repo) {
        this.notes = repo;
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Note> delete(@PathVariable("id") Long id){

        if (!notes.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        notes.deleteById(id);
        return ResponseEntity.ok().build();
    }


    /**
     * this returns all the Notes in complete form
     * <p>
     * Probably not what you want (Just the names and ids)
     * <p>
     * return All Notes with all Contents
     *
     * @return all the notes
     */
    @GetMapping(path = {"", "/"})
    public List<Note> getAll() {
        return notes.findAll();
    }


    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    @GetMapping("id/{id}")
    public ResponseEntity<Note> getById(@PathVariable("id") long id) {
        if (id < 0 || !notes.existsById(id))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(notes.findById(id).get());
    }

    /**
     * Get a list of all pairs of titles + ids.
     *
     * @return the list
     */
    @GetMapping("/list")
    public List<Pair<String, Long>> getAllNamesIds(){
        return getAll().stream()
                .map(note -> new Pair<String, Long>(note.title, note.id))
                .toList();
    }

    /**
     * Get all names list.
     *
     * @return the list
     */
    @GetMapping("/names")
    public List<String> getAllNames() {
        return getAll().stream().map(note -> note.title).toList();
    }


    /**
     * Get all ids list.
     *
     * @return the list
     */
    @GetMapping("/ids")
    public List<Long> getAllIds() {
        return getAll().stream().map(note -> note.id).toList();
    }

    /**
     * Search through notes using a keyword/string
     * Is case-insensitive to allow for more keywords
     * @param keyword the keyword used to search through the notes
     * @return the notes that have the keyword in title or contents
     */
    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam String keyword){
            List<Note> allNotes = notes.findAll();
            return allNotes.stream()
                    .filter(note -> note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || note.getText().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
    }

    /**
     * Add note to db.
     *
     * @param noteAdding the note adding
     * @return the response entity
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Note> addNote(@RequestBody Note noteAdding) {

        if (noteAdding.text == null || noteAdding.title == null)
            return ResponseEntity.badRequest().build();

        Note savedNote = notes.save(noteAdding);
        return ResponseEntity.ok(savedNote);
    }

    /**
     * Updates an existing note in the database.
     * @param id          the ID of the note to be updated
     * @param updatedNote the {@link Note} object containing the updated title and content
     * @return a ResponseEntity containing the updated object if the operation is successful,
     *         or a ResponseEntity with a bad request status if the note does not exist
     */

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note updatedNote) {
        if (!notes.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Note existingNote = notes.getReferenceById(id);
        existingNote.title = updatedNote.title;
        existingNote.text = updatedNote.text;
        return ResponseEntity.ok(existingNote);
    }

}
