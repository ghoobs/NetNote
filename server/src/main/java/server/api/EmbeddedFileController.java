package server.api;
import commons.EmbeddedFile;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;
import server.services.EmbeddedFileService;

/**
 * REST controller for managing embedded files within notes of a collection.
 * Provides endpoints to add, retrieve, delete, and rename files associated with specific notes.
 */
@RestController
@RequestMapping("/api")
public class EmbeddedFileController {

    private final NoteRepository noteRepo;
    private final EmbeddedFileService service;

    /**
     * Constructs an EmbeddedFileController with the specified repositories.
     *
     * @param noteRepo       the repository for managing notes
     * @param service        the embedded file service
     */
    public EmbeddedFileController(NoteRepository noteRepo,
                                  EmbeddedFileService service) {
        this.noteRepo = noteRepo;
        this.service = service;
    }

    @PostMapping("/{noteId}/files")
    public ResponseEntity<EmbeddedFile> addFile(@PathVariable long noteId,
                                                @RequestBody EmbeddedFile embeddedFile) {
        Note note = noteRepo.findById(noteId).orElse(null);
        if (note==null) {
            return ResponseEntity.notFound().build();
        }
        String url = "files/" + embeddedFile.getFilename();
        EmbeddedFile toSave = new EmbeddedFile(embeddedFile.getFilename(),
                embeddedFile.getFiletype(), url, embeddedFile.getData(), note);

        note.addEmbeddedFile(toSave);
        noteRepo.save(note);
        return ResponseEntity.ok(embeddedFile);
    }

    @GetMapping("/{noteId}/files/{filename}/data")
    public ResponseEntity<byte[]> getData(@PathVariable long noteId, @PathVariable String filename) {
        EmbeddedFile file = service.getEmbeddedFileFromNote(noteId, filename);
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = file.getData();
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{noteId}/files/{filename}")
    public ResponseEntity<Note> deleteFile(@PathVariable long noteId, @PathVariable String filename) {
        Note note = noteRepo.findById(noteId).orElse(null);
        if (note==null) {
            return ResponseEntity.notFound().build();
        }
        EmbeddedFile file = service.getEmbeddedFileFromNote(noteId, filename);
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        note.removeEmbeddedFile(file);
        noteRepo.save(note);
        return ResponseEntity.ok(note);
    }
    @PutMapping("/{noteId}/files/{filename}/rename/{newFilename}")
    public ResponseEntity<EmbeddedFile> renameFile(@PathVariable long noteId,
                                                   @PathVariable String filename, @PathVariable String newFilename) {
        EmbeddedFile file = service.getEmbeddedFileFromNote(noteId, filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        file.setFilename(newFilename);
        noteRepo.save(noteRepo.findById(noteId).get());
        return ResponseEntity.ok(file);
    }

}
