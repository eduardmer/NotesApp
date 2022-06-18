package com.notes.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notes {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private long createdDate;
    private long reminderDate;

    public Notes(int id, String title, String description, long createdDate, long reminderDate){
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.reminderDate = reminderDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(long reminderDate) {
        this.reminderDate = reminderDate;
    }
}
