package server.api;
import commons.EmbeddedFile;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.EmbeddedFileService;
import server.services.NoteService;

/**
 * REST controller for managing embedded files within notes of a collection.
 * Provides endpoints to add, retrieve, delete, and rename files associated with specific notes.
 */
@RestController
@RequestMapping("/api/embeds")
public class EmbeddedFileController {
    private final EmbeddedFileService embeddedFileService;
    private final NoteService noteService;

    /**
     * Constructs an EmbeddedFileController
     *
     * @param embeddedFileService   the embedded file service
     * @param noteService           the note services
     */
    public EmbeddedFileController(
            EmbeddedFileService embeddedFileService,
            NoteService noteService) {
        this.embeddedFileService = embeddedFileService;
        this.noteService = noteService;
    }

    @PostMapping("/{noteId}")
    public ResponseEntity<EmbeddedFile> addFile(@PathVariable long noteId,
                                                @RequestBody EmbeddedFile embeddedFile) {
        Note note = noteService.getNoteById(noteId);
        if (note==null) {
            return ResponseEntity.notFound().build();
        }
        String url = "files/" + embeddedFile.getFilename();
        EmbeddedFile toSave = new EmbeddedFile(embeddedFile.getFilename(),
                embeddedFile.getFiletype(), url, embeddedFile.getData(), note);

        note.addEmbeddedFile(toSave);
        noteService.saveNote(note);
        return ResponseEntity.ok(embeddedFile);
    }

    @GetMapping("/{noteId}/{filename}")
    public ResponseEntity<byte[]> getData(@PathVariable long noteId, @PathVariable String filename) {
        EmbeddedFile file = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = file.getData();
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{noteId}/{filename}")
    public ResponseEntity<Note> deleteFile(@PathVariable long noteId, @PathVariable String filename) {
        Note note = noteService.getNoteById(noteId);
        if (note==null) {
            return ResponseEntity.notFound().build();
        }
        EmbeddedFile file = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        note.removeEmbeddedFile(file);
        noteService.saveNote(note);
        return ResponseEntity.ok(note);
    }
    @PutMapping("/{noteId}/{filename}/rename")
    public ResponseEntity<EmbeddedFile> renameFile(@PathVariable long noteId,
                                                   @PathVariable String filename,
                                                   @RequestParam("name") String newFilename) {
        EmbeddedFile file = embeddedFileService.getEmbeddedFileFromNote(noteId, filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        file.setFilename(newFilename);
        noteService.updateNoteById(noteId);
        return ResponseEntity.ok(file);
    }

}
