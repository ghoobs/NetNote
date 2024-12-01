package server.database;


import org.springframework.data.jpa.repository.JpaRepository;

import commons.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
