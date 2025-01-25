package commons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    Pair a;
    Pair b;
    Pair c;

    @BeforeEach
    void setUp(){
        a = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        b = new Pair("Grocery List",
                """
                        - Milk
                        - Bananas
                        - Butter""");
        c = new Pair("Grocery List",
                " - Cookie");
    }

    @Test
    void constructorTest() {
        String field1 = "a";
        String field2 = "b";
        Pair<String, String> pair = new Pair<>(field1, field2);
        assertNotNull(pair);
    }

    @Test
    void emptyConstructorTest() {
        Pair pair = new Pair();
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
    }

    @Test
    void getSecondTest(){
        Object expected = """
                        - Milk
                        - Bananas
                        - Butter""";
        assertEquals(expected, a.getSecond());
    }

    @Test
    void getFirstTest(){
        Object expected = "Grocery List";
        assertEquals(expected, b.getFirst());
    }

    @Test
    void testEqualsTrue() {
        assertEquals(a, b);
    }

    @Test
    void testEqualsFalse() {
        assertNotEquals(a, c);
    }

    @Test
    void testHashCodeTrue() {
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testHashCodeFalse() {
        assertNotEquals(a.hashCode(), c.hashCode());
    }
}