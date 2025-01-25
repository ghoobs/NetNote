package server.api;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import commons.Note;
import commons.Pair;
import commons.Tag;
import events.AddEvent;
import events.DeleteEvent;
import events.UpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.services.NoteService;
import server.websocket.WebSocketMessaging;

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
    private final NoteService service;
    private ApplicationEventPublisher eventPublisher;
    private WebSocketMessaging messaging;

    /**
     * Instantiates a new Note controller.
     *
     * @param service Note service
     */
    public NoteController(NoteService service) {
        this.service = service;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Note> delete(@PathVariable("id") Long id){
        Note note = service.getNoteById(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        note.getEmbeddedFiles().clear();
        service.updateNoteById(id);
        service.eraseNoteById(id);
        DeleteEvent deleteEvent = new DeleteEvent(this, note);
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
        return service.getAllNotes();
    }


    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable("id") long id) {
        Note note = service.getNoteById(id);
        if (id < 0 || note == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(note);
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
    @GetMapping("/list/names")
    public List<String> getAllNames() {
        return getAll().stream().map(note -> note.title).toList();
    }


    /**
     * Get all ids list.
     *
     * @return the list
     */
    @GetMapping("/list/ids")
    public List<Long> getAllIds() {
        return getAll().stream().map(note -> note.id).toList();
    }

    /**
     * Get all tag list.
     *
     * @return the tags
     */
    @GetMapping("/list/tags")
    public List<Tag> getAllTags() {
        Set<Tag> tags = new HashSet<>();
        getAll().forEach(note -> tags.addAll(note.tags));
        return tags.stream().toList();
    }

    /**
     * Search through notes using a keyword/string
     * Is case-insensitive to allow for more keywords
     * @param keywords the keywords used to search through the notes
     * @return the notes that have the keyword in title or contents
     */
    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam("keywords") String keywords){
            List<Note> allNotes = service.getAllNotes();
            return allNotes.stream()
                    .filter(note -> note.hasKeyword(keywords))
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

        Note savedNote = service.saveNote(noteAdding);
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
        Note existingNote = service.getNoteById(id);
        if (existingNote == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean titleChanged = !existingNote.title.equals(updatedNote.title);
        if (titleChanged) {
            List<Note> renamedNotes = service.renameAllNoteReferences(
                    existingNote.title,
                    updatedNote.title,
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
        service.saveNote(existingNote);
        UpdateEvent updateEvent = new UpdateEvent(this, existingNote);
        eventPublisher.publishEvent(updateEvent);
        messaging.sendEvent(existingNote, "/topic/notes");
        return ResponseEntity.ok(existingNote);
    }
}
