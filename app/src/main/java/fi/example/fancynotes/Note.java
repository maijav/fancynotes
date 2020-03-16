package fi.example.fancynotes;

public class Note {
    private String text;
    private String thumbnail;

    public Note() {
    }

    public Note(String text, String thumbnail) {
        this.text = text;
        this.thumbnail = thumbnail;
    }


    public String getDescription() {
        return this.text;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setDescription(String text) {
        this.text = text;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
