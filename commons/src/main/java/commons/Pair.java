package commons;

import java.util.Objects;

public class Pair<T, G> {
    private T field1;
    private G field2;

    /**
     * Instantiates a new Pair.
     *
     * @param obj the first Object
     * @param obj2  the second Object
     */
    public Pair(T obj, G obj2){
        field1 = obj;
        field2 = obj2;
    }

    /**
     * Compares two Pairs
     * @param o other
     * @return true if the contents are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(field1, pair.field1) && Objects.equals(field2, pair.field2);
    }

    /**
     * Generates a hash code
     * @return a unique integer based off the contents of this Pair
     */
    @Override
    public int hashCode() {
        return Objects.hash(field1, field2);
    }
}
