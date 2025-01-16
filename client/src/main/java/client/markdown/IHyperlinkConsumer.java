package client.markdown;

public interface IHyperlinkConsumer {
    void onTagClick(String tag);
    void onNoteClick(String note);
    void onMiscLinkClick(String link);
}
