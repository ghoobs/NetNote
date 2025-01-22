package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Note class.
 * Data oriented for database usage.
 */
// Entity needed for Spring to be able to create a Database
@Entity
public class Note {
    public static final String REGEX_NAMING_FORMAT = "A-Za-z0-9\\s_\\.\\!\\?\\&\\=\\+\\-\\)\\(";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    public String text;

    @ManyToMany
    public List<Tag> tags;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmbeddedFile> embeddedFiles = new ArrayList<>();

    /**
     * Instantiates a new Note
     * Needed for Spring
     */
    public Note() {
        tags = new ArrayList<>();
    }

    /**
     * Instantiates a new Note with a title and text.
     *
     * @param title the title
     * @param text  the text
     */
    public Note(String title, String text) {
        this.title = title;
        this.text = text;
        tags = new ArrayList<>();
    }

    /**
     * Instantiates a new Note with a title, text and tags.
     *
     * @param title the title
     * @param text  the text
     * @param tags  the tags
     */
    public Note(String title, String text, List<Tag> tags) {
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    /**
     * Shallow copies the values of this note to the destination note
     * @param dst Note to shallow-copy the values into
     */
    public void copyTo(Note dst) {
        dst.title = this.title;
        dst.text = this.text;
        dst.tags = this.tags;
    }

    /**
     * Compares two Notes
     *
     * @param obj other
     * @return true if the contents are equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }


    /**
     * Generates a hash code
     *
     * @return a unique integer based off the contents of this Note
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A preview of the contents of this note
     *
     * @return title of the Note and a part of the contents
     */
    @Override
    public String toString() {
        String builder = "";
        if (title.isEmpty()) {
            builder += "Unnamed Note";
        } else {
            builder += title;
        }
        builder += "\n";
        if (text.isEmpty()) {
            builder += "*No contents*";
        } else {
            String[] lines = text.split("\\n");
            String block = lines[0];
            builder += block.substring(0, Math.min(30, block.length()));
            if (text.length() >= 30 || lines.length > 1) {
                builder += "...";
            }
        }
        return builder;
    }

    /**
     * Getter for the note's title
     *
     * @return String of the note title.
     */

    public String getTitle() {
        return title;
    }

    /**
     * Getter for the note's text
     *
     * @return String of the note's text.
     */

    public String getText() {
        return text;
    }

    /**
     * Getter for the note's id
     *
     * @return long of the note's id.
     */

    public long getId() {
        return id;
    }

    /**
     * Setter for the note's text
     *
     * @param text String of the new text.
     */

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Setter for the note's title
     *
     * @param title String of the new title.
     */

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the list of embedded files associated with this note.
     *
     * @return a list of {@link EmbeddedFile} objects associated with the note
     */
    public List<EmbeddedFile> getEmbeddedFiles() {
        return embeddedFiles;
    }

    /**
     * Sets the list of embedded files for this note.
     *
     * @param embeddedFiles the new list of {@link EmbeddedFile} objects to associate with the note
     */
    public void setEmbeddedFiles(ArrayList<EmbeddedFile> embeddedFiles) {
        this.embeddedFiles = embeddedFiles;
    }

    /**
     * Adds an embedded file to the list of files associated with this note.
     * The file's note reference is updated to point to this note.
     *
     * @param file the {@link EmbeddedFile} to add
     * @return true if the file was successfully added, false otherwise
     */
    public boolean addEmbeddedFile(EmbeddedFile file) {
        file.setNote(this);
        return embeddedFiles.add(file);
    }

    /**
     * Removes an embedded file from the list of files associated with this note.
     * The file's note reference is cleared.
     *
     * @param file the {@link EmbeddedFile} to remove
     * @return true if the file was successfully removed, false otherwise
     */
    public boolean removeEmbeddedFile(EmbeddedFile file) {
        file.setNote(null);
        return embeddedFiles.remove(file);
    }

    /**
     * Checks if the keyword is related in the title, body, or tags.
     * @param keyword Keyword to check
     * @return true if the keyword is found in the aforementioned elements, or if the elements are present in the keyword.
     */
    public boolean hasKeyword(String keyword) {
        String lowerKw = keyword.toLowerCase();
        String lowerTitle= title.toLowerCase();
        if (lowerTitle.contains(lowerKw) || lowerKw.contains(lowerTitle)) {
            return true;
        }
        String lowerText = text.toLowerCase();
        if (lowerText.contains(lowerKw) || lowerKw.contains(lowerText)) {
            return true;
        }
        for (Tag tag : tags) {
            String lowerTag = tag.name.toLowerCase();
            if (lowerTag.contains(lowerKw) || lowerKw.contains(lowerTag)) {
                return true;
            }
        }
        return false;
    }
}
