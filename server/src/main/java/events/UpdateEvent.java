package events;

import commons.Note;
import org.springframework.context.ApplicationEvent;

/**
 * used for automated change synchronization
 * when deleting event is called, web socket is used to push to client
 */
public class UpdateEvent extends ApplicationEvent {
    private final Note note;

    public UpdateEvent(Object source, Note note) {
        super(source);
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
