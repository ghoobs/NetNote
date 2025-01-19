package client.markdown;

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
     * Called to ask if a note with this name exists, within the same collection.
     * This is used to determine the style of the button.
     * @param note Name of the note, excluding the [[...]]
     * @return Must return true if the note exists
     */
    boolean doesNoteExistWithinSameCollection(String note);
}
