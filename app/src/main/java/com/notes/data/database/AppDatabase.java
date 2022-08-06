package com.notes.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.notes.data.entities.Notes;

@Database(entities = {Notes.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NotesDao getNotesDao();

}
