package events;

import commons.Note;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * simple test class for full coverage for the events classes
 */
class AddEventTest {

    @Test
    public void AddEventConstructorTest() {
        Note note = new Note("Test", "test note");
        note.id = 123;
        AddEvent event = new AddEvent(this, note);

        assertNotNull(event);
        assertEquals(note, event.getNote());
        assertEquals(123, event.getNote().id);
        assertEquals(this, event.getSource());
    }
}