package client.markdown;

public interface IHyperlinkConsumer {
    /**
     * Called when a #Tag is clicked.
     * @param tag Name of the tag, excluding the #...
     */
    void onTagClick(String tag);
    /**
     * Called when a [[Note]] is clicked.
     * @param note Name of the note, excluding the [[...]]
     */
    void onNoteClick(String note);
    /**
     * Called when a standard URL is clicked.
     * @param url Value of the url
     */
    void onUrlClick(String url);
}
