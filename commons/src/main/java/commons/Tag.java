package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Tag {
    public static final String REGEX_NAMING_FORMAT = "A-Za-z0-9\\_\\.\\-";
    public static final String REGEX_MD_TAG_REFERENCE = getMarkdownRegex(REGEX_NAMING_FORMAT);

    /**
     * Constructs a markdown regex
     * @param matcher What to match
     * @return Regular expression
     */
    public static String getMarkdownRegex(String matcher) {
        return "#(["+ matcher+"]+)";
    }

    @Id
    public String name;

    /**
     * Initializes Tag
     */
    public Tag() {
        name = "";
    }

    /**
     * Initializes Tag with a proper value
     * @param name Value of the tag
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Hash code of the tag
     * @return Hash code of the tag's value
     */
    @Override
    public int hashCode() {
        return name.hashCode();
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
        return Objects.equals(name, tag1.name);
    }

    /**
     * Converts this Tag to a string
     * @return The value of this Tag
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if the Tag contains a valid value
     * @return True if the tag's value is non-null and not empty
     */
    public boolean valid() {
        return name != null && !name.isEmpty();
    }
}
