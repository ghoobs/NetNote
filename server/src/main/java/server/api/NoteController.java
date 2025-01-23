package server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import commons.Collection;
import commons.Note;
import commons.CollectionNote;
import commons.Pair;
import commons.Tag;
import events.AddEvent;
import events.DeleteEvent;
import events.UpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import server.websocket.WebSocketMessaging;
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
    private ApplicationEventPublisher eventPublisher;
    private WebSocketMessaging messaging;
    /**
     * Instantiates a new Note controller.
     *
     * @param repo the repo
     */
    public NoteController(NoteRepository repo) {
        this.notes = repo;
    }

    /**
     * setter for ApplicationEventPublisher
     *
     * @param eventPublisher the application event publisher
     */
    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setWebSocketMessaging(WebSocketMessaging messaging) {
        this.messaging = messaging;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Note> delete(@PathVariable("id") Long id){

        if (!notes.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Note toDelete = notes.findById(id).get();

        // destroy orphans
        toDelete.getEmbeddedFiles().clear();
        notes.save(toDelete); // destroy orphans by clearing list and saving

        notes.deleteById(id);
        DeleteEvent deleteEvent = new DeleteEvent(this, toDelete);
        eventPublisher.publishEvent(deleteEvent);
        messaging.sendEvent(id, "/topic/notes");
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
                .map(note -> new Pair<>(note.title, note.id))
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
     * Get all tag list.
     *
     * @return the tags
     */
    @GetMapping("/tags")
    public List<Tag> getAllTags() {
        Set<Tag> tags = new HashSet<>();
        getAll().forEach(note -> tags.addAll(note.tags));
        return tags.stream().toList();
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
                    .filter(note -> note.hasKeyword(keyword))
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
        AddEvent addEvent = new AddEvent(this, savedNote);
        eventPublisher.publishEvent(addEvent);
        messaging.sendEvent(savedNote, "/topic/notes");
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
    public ResponseEntity<Note> updateNote(@PathVariable("id") Long id, @RequestBody Note updatedNote) {
        if (!notes.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Note existingNote = notes.findById(id).get();
        boolean titleChanged = !existingNote.title.equals(updatedNote.title);
        if (titleChanged) {
            List<Note> renamedNotes = renameAllNoteReferences(existingNote.title, updatedNote.title,
                    null /* TODO: add note collection here in the future*/);
            if (messaging != null) {
                renamedNotes.forEach(
                    (note) -> { // send update for notes that have been modified
                        messaging.sendEvent(note, "/topic/notes");
                    }
                );
            }
        }
        updatedNote.copyTo(existingNote);

        notes.save(existingNote);
        UpdateEvent updateEvent = new UpdateEvent(this, existingNote);
        eventPublisher.publishEvent(updateEvent);
        messaging.sendEvent(existingNote, "/topic/notes");
        return ResponseEntity.ok(existingNote);
    }

    /**
     * Gets all notes pertaining to a specific collection
     * @param collection Collection to search in
     * @return List of notes contained in the collection
     */
    private List<Note> getNotesFromCollection(Collection collection) {
        if (collection == null) {
            //throw new IllegalArgumentException("Collection must not be null!");
            return notes.findAll();
        }
        return notes.findAll()
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
    private List<Note> renameAllNoteReferences(String oldTitle, String newTitle, Collection collection) {
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
