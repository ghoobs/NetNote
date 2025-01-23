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

/**
 * Represents an embedded file associated with a note.
 * The file can include metadata such as filename, file type, URL, and binary data.
 */
@Entity
public class EmbeddedFile {
    // prevent illegal Windows file names + whitespaces excl. spaces
    public static final String REGEX_ALT_NAMING_FORMAT = "[^\\n\\r\\t]+";
    public static final String REGEX_URL_NAMING_FORMAT = "[^\\n\\r\\t\\:\\*\\/" +
            "\\\"\\|\\?\\\"\\<\\>\\\\]+";
    public static final String REGEX_MD_EMBED_REFERENCE =
            getMarkdownRegex(REGEX_ALT_NAMING_FORMAT,REGEX_URL_NAMING_FORMAT);

    /**
     * Constructs a markdown regex
     * @param altMatcher What to match (alt text)
     * @param urlMatcher What to match (url)
     * @return Regular expression
     */
    public static String getMarkdownRegex(String altMatcher, String urlMatcher) {
        return "!\\[(" +altMatcher + ")\\]" +
                "\\(("+ urlMatcher +")\\)";
    }

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


    public EmbeddedFile() {}

    /**
     * Constructs an EmbeddedFile for client side usage.
     * @param filename name of the file (no directory)
     * @param filetype file extension
     * @param data contents of the file.
     */
    public EmbeddedFile(String filename, String filetype, byte[] data) {
        this.filename = filename;
        this.filetype = filetype;
        this.data = data;
    }

    public EmbeddedFile(String filename, String filetype, String url, long id, byte[] data, Note note) {
        this.filename = filename;
        this.filetype = filetype;
        this.url = url;
        this.id = id;
        this.data = data;
        this.note = note;
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

    /**
     * Sets the filename of the embedded file.
     *
     * @param filename the new filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the file type of the embedded file.
     *
     * @return the file type
     */
    public String getFiletype() {
        return filetype;
    }

    /**
     * Sets the file type of the embedded file.
     *
     * @param filetype the new file type
     */
    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    /**
     * Gets the URL associated with the embedded file.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }


    /**
     * Sets the URL associated with the embedded file.
     *
     * @param url the new URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the unique identifier of the embedded file.
     *
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the embedded file.
     *
     * @param id the new ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the binary data of the embedded file.
     *
     * @return the binary data as a byte array
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the binary data of the embedded file.
     *
     * @param data the new binary data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the note to which this file belongs.
     *
     * @return the associated note
     */
    public Note getNote() {
        return note;
    }

    /**
     * Sets the note to which this file belongs.
     *
     * @param note the new associated note
     */
    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * Checks if this EmbeddedFile is equal to another object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
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

    /**
     * Computes the hash code for this EmbeddedFile.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getFiletype(), getUrl(), getId(),
        Arrays.hashCode(getData()), getNote());
    }

    /**
     * Returns a string representation of the EmbeddedFile.
     *
     * @return a string containing the filename, file type, URL, ID, and associated note
     */
    @Override
    public String toString() {
        return "EmbeddedFile{" + "filename='" + filename + '\'' + ", filetype='" + filetype + '\'' +
                ", url='" + url + '\'' + ", id=" + id + ", note=" + note + '}';
    }
}