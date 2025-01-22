package server.api;
import commons.Collection;
import commons.CollectionNote;
import commons.EmbeddedFile;
import commons.Note;
import jakarta.annotation.Nullable;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.EmbeddedFileRepository;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing embedded files within notes of a collection.
 * Provides endpoints to add, retrieve, delete, and rename files associated with specific notes.
 */
@RestController
@RequestMapping("/api")
public class EmbeddedFileController {

    private final NoteRepository noteRepo;
    private final CollectionRepository collectionRepo;

    /**
     * Constructs an EmbeddedFileController with the specified repositories.
     *
     * @param noteRepo       the repository for managing notes
     * @param collectionRepo the repository for managing collections
     */
    public EmbeddedFileController(NoteRepository noteRepo,
                                  CollectionRepository collectionRepo) {
        this.noteRepo = noteRepo;
        this.collectionRepo = collectionRepo;
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
        EmbeddedFile file = getEmbeddedFileFromNote(noteId, filename);
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = file.getData();
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{noteId}/files/{filename}")
    public ResponseEntity<Note> deleteFile(@PathVariable long collectionId, @PathVariable String noteTitle, @PathVariable String filename) {
        Optional<Collection> optionalCollection = collectionRepo.findById(collectionId);
        if (optionalCollection.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CollectionNote> notes = optionalCollection.get().notes;

        Note noteWithTitle = null;
        for (Note note : notes) {
            if (note.getTitle().equals(noteTitle)) {
                noteWithTitle = note;
            }
        }
        if (noteWithTitle==null) {
            return ResponseEntity.notFound().build();
        }

        List<EmbeddedFile> files = noteWithTitle.getEmbeddedFiles();

        EmbeddedFile file = null;
        for (EmbeddedFile embeddedFile : files) {
            if (embeddedFile.getFilename().equals(filename)) {
                file = embeddedFile;
            }
        }
        if(file == null) {
            return ResponseEntity.notFound().build();
        }

        noteWithTitle.removeEmbeddedFile(file);
        noteRepo.save(noteWithTitle);
        return ResponseEntity.ok(noteWithTitle);
    }
    @PutMapping("/{noteId}/files/{filename}/rename/{newFilename}")
    public ResponseEntity<EmbeddedFile> renameFile(@PathVariable long noteId,
                                                   @PathVariable String filename, @PathVariable String newFilename) {
        EmbeddedFile file = getEmbeddedFileFromNote(noteId, filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        file.setFilename(newFilename);
        noteRepo.save(noteRepo.findById(noteId).get());
        return ResponseEntity.ok(file);
    }

    /**
     * Gets list of embedded files
     * @param noteId id of the note
     * @return List of embeds. Will be null if the note doesn't exist
     */
    private @Nullable List<EmbeddedFile> getAllEmbeddedFilesFromNote(long noteId) {
        Optional<Note> optionalNote = noteRepo.findById(noteId);
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
    private @Nullable EmbeddedFile getEmbeddedFileFromNote(long noteId, String filename) {
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
