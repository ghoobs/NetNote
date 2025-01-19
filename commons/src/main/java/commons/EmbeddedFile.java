package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.util.Arrays;
import java.util.Objects;

@Entity
public class EmbeddedFile {
    private String filename;
    private String filetype;
    private String url;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    @JsonBackReference
    private Note note;

    public EmbeddedFile(String filename, String filetype, String url, long id, byte[] data, Note note) {
        this.filename = filename;
        this.filetype = filetype;
        this.url = url;
        this.id = id;
        this.data = data;
        this.note = note;
    }

    public EmbeddedFile() {
    }

    public EmbeddedFile(String filename, String filetype, String url, byte[] data, Note note) {
        this.filename = filename;
        this.filetype = filetype;
        this.url = url;
        this.data = data;
        this.note = note;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddedFile that = (EmbeddedFile) o;
        return getId() == that.getId()
        && Objects.equals(getFilename(), that.getFilename())
        && Objects.equals(getFiletype(), that.getFiletype())
        && Objects.equals(getUrl(), that.getUrl())
        && Objects.deepEquals(getData(), that.getData())
        && Objects.equals(getNote(), that.getNote());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getFiletype(), getUrl(), getId(),
        Arrays.hashCode(getData()), getNote());
    }

    @Override
    public String toString() {
        return "EmbeddedFile{" + "filename='" + filename + '\'' + ", filetype='" + filetype + '\'' +
                ", url='" + url + '\'' + ", id=" + id + ", note=" + note + '}';
    }
}