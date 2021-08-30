package com.example.notesonfire.models;

import java.io.Serializable;

public class Note implements Serializable {

    private String noteTitle;
    private String noteDate;
    private String noteText;
    private int noteColor;
    private String noteWebUrl;
    private String noteImagePath;

    public Note() {
    }

    public Note(String noteTitle, String noteDate, String noteText, int noteColor, String noteWebUrl, String noteImagePath) {
        this.noteTitle = noteTitle;
        this.noteDate = noteDate;
        this.noteText = noteText;
        this.noteColor = noteColor;
        this.noteWebUrl = noteWebUrl;
        this.noteImagePath = noteImagePath;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(int noteColor) {
        this.noteColor = noteColor;
    }

    public String getNoteWebUrl() {
        return noteWebUrl;
    }

    public void setNoteWebUrl(String noteWebUrl) {
        this.noteWebUrl = noteWebUrl;
    }

    public String getNoteImagePath() {
        return noteImagePath;
    }

    public void setNoteImagePath(String noteImagePath) {
        this.noteImagePath = noteImagePath;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteTitle='" + noteTitle + '\'' +
                ", noteDate='" + noteDate + '\'' +
                ", noteText='" + noteText + '\'' +
                ", noteColor=" + noteColor +
                ", noteWebUrl='" + noteWebUrl + '\'' +
                ", noteImagePath='" + noteImagePath + '\'' +
                '}';
    }
}
