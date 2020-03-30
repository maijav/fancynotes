package fi.example.fancynotes;

import android.os.Build;

import java.util.Optional;

public class Note {
    int id;
    private String title;
    private String note;
    private String imageUri;
    private String voiceUri;
    int orderId;
    private String noteBackground;
    private String tags;

    public Note() {
    }

    public Note(int id, int orderId, String title, String note, String noteBackground, String imageUri, String voiceUri, String tags) {
        this.id = id;
        this.orderId = orderId;
        this.title = title;
        this.note = note;
        this.noteBackground = noteBackground;
        this.imageUri = imageUri;
        this.voiceUri = voiceUri;
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getVoiceUri() {
        return voiceUri;
    }

    public void setVoiceUri(String voiceUri) {
        this.voiceUri = voiceUri;
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

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }
}
