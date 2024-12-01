package commons;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void constructorTest() {
        String field1 = "a";
        String field2 = "b";
        Pair<String, String> pair = new Pair<>(field1, field2);
        Pair<String, String> expected = new Pair<>(field1, field2);

        assertEquals(expected, pair);
    }

    @Test
    void testEquals() {
        Pair a = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        Pair b = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        Pair c = new Pair("Grocery List",
                " - Cookie");

        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void testHashCode() {
        Pair a = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        Pair b = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        Pair c = new Pair("Grocery List",
                " - Cookie");

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }
}