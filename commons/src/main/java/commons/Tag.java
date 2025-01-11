package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Tag {
    @Id
    public String tag;

    /**
     * Initializes Tag
     */
    public Tag() {}

    /**
     * Initializes Tag with a proper value
     * @param tag Value of the tag
     */
    public Tag(String tag) {
        this.tag = tag;
    }

    /**
     * Hash code of the tag
     * @return Hash code of the tag's value
     */
    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    /**
     * Compares this Tag to another object
     * @param o Other object. Preferably another Tag
     * @return if this Tag is equal to the other Tag
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag1 = (Tag) o;
        return Objects.equals(tag, tag1.tag);
    }

    /**
     * Converts this Tag to a string
     * @return The value of this Tag
     */
    @Override
    public String toString() {
        return tag;
    }

    /**
     * Checks if the Tag contains a valid value
     * @return True if the tag's value is non-null and not empty
     */
    public boolean valid() {
        return tag != null && !tag.isEmpty();
    }
}
