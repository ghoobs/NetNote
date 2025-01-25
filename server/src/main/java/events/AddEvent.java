package events;

import commons.Note;
import org.springframework.context.ApplicationEvent;

/**
 * used for automated change synchronization
 * when adding event is called, web socket is used to push to client
 */
public class AddEvent extends ApplicationEvent {
    private final Note note;

    public AddEvent(Object source, Note note) {
        super(source);
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}