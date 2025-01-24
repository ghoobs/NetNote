package client.markdown;

import commons.Note;

public interface IMarkdownEvents {
    /**
     * Called when a #Tag is clicked.
     * @param tag Name of the tag, excluding the #...
     */
    void onTagMdButtonClick(String tag);
    /**
     * Called when a [[Note]] is clicked.
     * @param note Name of the note, excluding the [[...]]
     */
    void onNoteMdButtonClick(String note);
    /**
     * Called when a standard URL is clicked.
     * @param url Value of the url
     */
    void onUrlMdAnchorClick(String url);

    /**
     * Called when an embedded file is clicked
     * @param fileName Name of the file
     */
    void onMdEmbeddedFileClick(String fileName);

    /**
     * Called to ask if a note with this name exists, within the same collection.
     * This is used to determine the style of the button.
     * @param note Name of the note, excluding the [[...]]
     * @return Must return true if the note exists
     */
    boolean doesNoteExistWithinSameCollection(String note);

    /**
     * Called to get the currently editing note
     * @return Valid note
     */
    Note getSelectedNote();

    /**
     * Called to get the url of the remote server
     * @return A valid HTTP url pointing to the server.
     */
    String getServerUrl();
}
