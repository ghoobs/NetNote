package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection class.
 * Data oriented for database usage.
 */
@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String name;

    //@JsonIgnoreProperties({"collection"})
    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
    public List<CollectionNote> notes;

    /**
     * Instantiates a new Collection
     * Needed for Spring
     */
    public Collection() {
    }

    /**
     * Instantiates a new empty Collection with a name
     *
     * @param name the name
     */
    public Collection(String name) {
        this.name = name;
        this.notes = new ArrayList<>();
    }

    /**
     * Instantiates a new Collection with a name and Notes.
     *
     * @param name the name
     * @param notes  the Notes
     */
    public Collection(String name, List<CollectionNote> notes) {
        this.name = name;
        this.notes = notes;
        for (CollectionNote note : notes) {
            note.setCollection(this);
        }
    }

    public void addNote(CollectionNote note) {
        notes.add(note);
        note.setCollection(this);
    }

    /**
     * Compares two Collections
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
     * @return a unique integer based off the contents of this Collection
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A preview of the contents of this Collection
     *
     * @return name of the Collection and the titles of the Notes it contains
     */
    @Override
    public String toString() {
        String result = "";
        if (name.isEmpty()) {
            result += "Unnamed Collection";
        } else {
            result += name;
        }
        result += "\n";

        for(Note note : notes){
            //System.out.println(note);
            if (note.title.isEmpty()) {
                result += "Unnamed Note\n";
            } else {
                result += note.title + "\n";
            }
        }
        return result;
    }
}
