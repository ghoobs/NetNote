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

@RestController
@RequestMapping("/api")
public class EmbeddedFileController {

    private final NoteRepository noteRepo;
    private final CollectionRepository collectionRepo;

    public EmbeddedFileController(NoteRepository noteRepo,
                                  CollectionRepository collectionRepo) {
        this.noteRepo = noteRepo;
        this.collectionRepo = collectionRepo;
    }

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
