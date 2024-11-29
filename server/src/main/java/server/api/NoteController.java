package server.api;

import java.util.List;

import commons.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public NoteController(NoteRepository repo){
        this.notes = repo;
    }


    /**
     * this returns all the Notes in complete form
     *
     * Probably not what you want (Just the names and ids)
     *
     * return All Notes with all Contents
     *
     * @return all the notes
     */
    @GetMapping(path = {"","/"})
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
    public ResponseEntity<Note> getById(@PathVariable("id") long id){
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
    public List<String> getAllNames(){
        return getAll().stream().map(note -> note.title).toList();
    }


    /**
     * Get all ids list.
     *
     * @return the list
     */
    @GetMapping("/ids")
    public List<Long> getAllIds(){
        return getAll().stream().map(note -> note.id).toList();
    }

    /**
     * Add note to db.
     *
     * @param noteAdding the note adding
     * @return the response entity
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Note> addNote(@RequestBody Note noteAdding){

        if (noteAdding.text==null || noteAdding.title == null)
            return ResponseEntity.badRequest().build();

        Note savedNote = notes.save(noteAdding);
        return ResponseEntity.ok(savedNote);
    }


}
