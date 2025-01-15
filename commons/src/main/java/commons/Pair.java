package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


public class Pair<T, G>  {

    private T first;

    private G second;

    /**
     * Instantiates a new Pair.
     *
     * @param obj the first Object
     * @param obj2  the second Object
     */
    public Pair(T obj, G obj2){
        first = obj;
        second = obj2;
    }

    public Pair(){
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
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    /**
     * Generates a hash code
     * @return a unique integer based off the contents of this Pair
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(G second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    public T getFirst() {
        return first;
    }

    public G getSecond() {
        return second;
    }
}
