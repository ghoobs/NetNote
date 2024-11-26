package commons;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Note class.
 * Data oriented for database usage.
 */
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    public String text;

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
     * @param obj other
     * @return true if the contents are equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates a hash code
     * @return a unique integer based off the contents of this Note
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A preview of the contents of this note
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
            String[] lines =  text.split("\\n");
            String block =lines[0];
            builder += block.substring(0, Math.min(30, block.length()));
            if (text.length() >= 30 || lines.length > 1) {
                builder += "...";
            }
        }
        return builder ;
    }
}
