package fi.example.fancynotes;

public class Note {
    int id;
    private String title;
    private String note;
    private String noteBackground;

    public Note() {
    }

    public Note(int id, String title, String note, String noteBackground) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.noteBackground = noteBackground;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNoteBackground() {
        return noteBackground;
    }

    public void setNoteBackground(String noteBackground) {
        this.noteBackground = noteBackground;
    }
}
