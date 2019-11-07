package ratingapp.ddey.com.testratingapp.models;

import java.io.Serializable;

public class Note implements Serializable {
    private long idNote;
    private String text;
    private String noteToken;
    private String userToken;


    public Note() {

    }

    public Note(String text) {
        this.text = text;
    }

    public Note(long idNote, String text, String noteToken, String userToken) {
        this.idNote = idNote;
        this.text = text;
        this.noteToken = noteToken;
        this.userToken = userToken;
    }

    public long getIdNote() {
        return idNote;
    }

    public void setIdNote(long idNote) {
        this.idNote = idNote;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNoteToken() {
        return noteToken;
    }

    public void setNoteToken(String noteToken) {
        this.noteToken = noteToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @Override
    public String toString() {
        return "Note{" +
                "idNote=" + idNote +
                ", text='" + text + '\'' +
                ", noteToken='" + noteToken + '\'' +
                ", userToken='" + userToken + '\'' +
                '}';
    }
}
