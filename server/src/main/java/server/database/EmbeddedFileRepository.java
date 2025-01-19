package server.database;

import commons.EmbeddedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddedFileRepository extends JpaRepository<EmbeddedFile, Long> {
}
