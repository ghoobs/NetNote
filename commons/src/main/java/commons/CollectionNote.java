package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class CollectionNote extends Note {
    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collection", nullable = false)
    @JsonIgnoreProperties({"notes"})
    public Collection collection;*/
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    public Collection collection;

    public CollectionNote() {
    }

    public CollectionNote(String title, String text) {
        super(title, text);
    }

    public CollectionNote(String title, String text, Collection collection) {
        super(title, text);
        this.collection = collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return
                "collection=" + collection + "\n" + super.toString();
    }


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }


    @Override
    public int hashCode() {
        //System.out.println(Objects.hash(collection));
        return HashCodeBuilder.reflectionHashCode(this, "collection");
    }


}
