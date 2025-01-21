package server.api;
import commons.Collection;
import commons.CollectionNote;
import commons.EmbeddedFile;
import commons.Note;
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

    /**
     * Adds a new embedded file to a specific note within a collection.
     *
     * @param collectionId the ID of the collection
     * @param noteTitle    the title of the note
     * @param embeddedFile the embedded file to add
     * @return a ResponseEntity containing the added EmbeddedFile if successful,
     * or a 404 response if the collection or note is not found
     */
    @GetMapping("/{collectionId}/{noteTitle}/files/upload")
    @PostMapping
    public ResponseEntity<EmbeddedFile> addFile(@PathVariable long collectionId, @PathVariable String noteTitle,
                                                @RequestBody EmbeddedFile embeddedFile) {
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

        EmbeddedFile toSave = new EmbeddedFile(embeddedFile.getFilename(),
                embeddedFile.getFiletype(), embeddedFile.getUrl(), embeddedFile.getData(), noteWithTitle);

        noteWithTitle.addEmbeddedFile(toSave);
        noteRepo.save(noteWithTitle);

        return ResponseEntity.ok(embeddedFile);
    }

    /**
     * Retrieves the binary data of a specific embedded file within a note.
     *
     * @param collectionId the ID of the collection
     * @param noteTitle    the title of the note
     * @param filename     the name of the file to retrieve
     * @return a ResponseEntity containing the file's binary data if successful,
     * or a 404 response if the collection, note, or file is not found
     */
    @GetMapping("/{collectionId}/{noteTitle}/{filename}")
    public ResponseEntity<byte[]> getData(@PathVariable long collectionId, @PathVariable String noteTitle, @PathVariable String filename) {
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

        byte[] data = file.getData();
        return ResponseEntity.ok(data);
    }

    /**
     * Deletes a specific embedded file from a note within a collection.
     *
     * @param collectionId the ID of the collection
     * @param noteTitle    the title of the note
     * @param filename     the name of the file to delete
     * @return a ResponseEntity containing the updated Note if successful,
     * or a 404 response if the collection, note, or file is not found
     */
    @DeleteMapping("/{collectionId}/{noteTitle}/{filename}/delete")
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

    /**
     * Renames a specific embedded file within a note.
     *
     * @param collectionId the ID of the collection
     * @param noteTitle    the title of the note
     * @param filename     the current name of the file
     * @param newFilename  the new name for the file
     * @return a ResponseEntity containing the updated EmbeddedFile if successful,
     * or a 404 response if the collection, note, or file is not found
     */
    @GetMapping("/{collectionId}/{noteTitle}/{filename}/{newFilename}/rename")
    public ResponseEntity<EmbeddedFile> renameFile(@PathVariable long collectionId, @PathVariable String noteTitle,
                                                   @PathVariable String filename, @PathVariable String newFilename) {
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

        file.setFilename(newFilename);
        noteRepo.save(noteWithTitle);
        return ResponseEntity.ok(file);
    }
}
