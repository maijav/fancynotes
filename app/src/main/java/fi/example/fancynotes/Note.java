package fi.example.fancynotes;

import java.util.Calendar;

/**
 * The Note class is used when creating new note objects from the backend to be used and displayed in the app.
 * The Note object collects all the needed information that each note should have.
 *
 * @author Hanna Tuominen
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */
public class Note {
    int id;
    private String title;
    private String note;
    private String imageUri;
    private String voiceUri;
    int orderId;
    private String noteBackground;
    private String tags;
    private Calendar date;

    /**
     * Empty constructor
     */
    public Note() {
    }

    /**
     * Constructor for creating new notes with all info available
     * @param id if of the note
     * @param orderId orderid of the note
     * @param title tittle of the note
     * @param note text of the note
     * @param noteBackground backhroundcolor of the note
     * @param imageUri image uri of the note
     * @param voiceUri voice uri of the note
     * @param tags tags of the note
     * @param date timer date of the note
     */
    public Note(int id, int orderId, String title, String note, String noteBackground, String imageUri, String voiceUri, String tags, Calendar date) {
        this.id = id;
        this.orderId = orderId;
        this.title = title;
        this.note = note;
        this.noteBackground = noteBackground;
        this.imageUri = imageUri;
        this.voiceUri = voiceUri;
        this.tags = tags;
        this.date = date;

    }

    /**
     * get date
     * @return the date for the timed note
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * set the timed note date
     * @param date the calencar date to be set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /***
     * get the tags of the note
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * set the tags of the note
     * @param tags the tags of the note
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * get the order if of the note
     * @return the order id of the note
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * set the order id of the note
     * @param orderId the order id of the note
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * get the voice uri of the note
     * @return the voice uri of the note
     */
    public String getVoiceUri() {
        return voiceUri;
    }

    /**
     * set the voice uri of the note
     * @param voiceUri the voice uri of the note
     */
    public void setVoiceUri(String voiceUri) {
        this.voiceUri = voiceUri;
    }

    /**
     * get the id of the note
     * @return the id of the note
     */
    public int getId() {
        return id;
    }

    /**
     * set the id of the note
     * @param id the id of the note
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * get the tittle of the note
     * @return the tittle of the note
     */
    public String getTitle() {
        return title;
    }

    /**
     * set the tittle of the note
     * @param title the tittle of the note
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get the text of the note
     * @return the text of the note
     */
    public String getNote() {
        return note;
    }

    /**
     * set the text of the note
     * @param note the text of the note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * get the background color of the note
     * @return the background color of the note
     */
    public String getNoteBackground() {
        return noteBackground;
    }

    /**
     * set the background color of the note
     * @param noteBackground the background color of the note
     */
    public void setNoteBackground(String noteBackground) {
        this.noteBackground = noteBackground;
    }

    /**
     * set the image uri of the note
     * @param imageUri the image uri of the note
     */
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * get the image uri of the note
     * @return the image uri of the note
     */
    public String getImageUri() {
        return imageUri;
    }
}
