package client.utils;

import commons.Note;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.checkerframework.checker.units.qual.C;
import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils2 {
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Retrieves notes from the server using a low-level HTTP request.
     * Reads and prints each line from the response.
     *
     * @throws IOException        if an I/O error occurs when reading the server response
     * @throws URISyntaxException if the constructed URL is invalid
     */

    public void getNotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/notes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Retrieves all notes from the server.
     *
     * @return a list of {@link Note} objects fetched from the server
     */

    public List<Note> getNotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/notes") //
                .request(APPLICATION_JSON) //
                .get(new GenericType<List<Note>>() {
                });
    }

    /**
     * Adds a new note to the server.
     *
     * @param note the {@link Note} object to be added
     * @return the {@link Note} object as returned by the server after addition
     */

    public Note addNote(Note note) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/notes") //
                .request(APPLICATION_JSON) //
                .post(Entity.entity(note, APPLICATION_JSON), Note.class);
    }

    /**
     * Checks if the server is available by attempting a connection.
     *
     * @return {@code true} if the server is reachable, {@code false} otherwise
     */

    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(SERVER) //
                    .request(APPLICATION_JSON) //
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }

    /**
     * Update an existing note on the server.
     *
     * @param note The note object with updated title and content.
     * @return The updated Note object from the server.
     */

    public Note updateNote(Note note) {
        System.out.println("updateNote in serverutils2");
        System.out.println(note.getId());
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/notes/" + note.getId()) // Endpoint for updating a note
                .request(APPLICATION_JSON) //
                .put(Entity.entity(note, APPLICATION_JSON), Note.class);
    }

    public List<Note> searchNotes(String keyword) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/notes/search")
                .queryParam("keyword", keyword)
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Note>>() {});
    }

    /**
     * Delete an existing note on the server
     *
     * @param note the note object that wil be deleted
     */
    public void deleteNote(Note note) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/notes/delete/" + note.getId()) //Endpoint for deleting a note
                .request(APPLICATION_JSON)
                .delete();
    }
}
