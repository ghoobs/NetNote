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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    public String text;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmbeddedFile> embeddedFiles = new ArrayList<>();

    /**
     * Instantiates a new Note
     * Needed for Spring
     */
    // Needed for Spring to initialize an object
    // Otherwise the runtime throws an error
    public Note() {
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

//    public List<EmbeddedFile> getEmbeddedFiles() {
//        return embeddedFiles;
//    }
//
//    public void setEmbeddedFiles(ArrayList<EmbeddedFile> embeddedFiles) {
//        this.embeddedFiles = embeddedFiles;
//    }
//
//    public boolean addEmbeddedFile(EmbeddedFile file) {
//        file.setNote(this);
//        return embeddedFiles.add(file);
//    }
//
//    public boolean removeEmbeddedFile(EmbeddedFile file) {
//        file.setNote(null);
//        return embeddedFiles.remove(file);
//    }
}
