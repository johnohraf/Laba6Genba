package com.example.laba6genba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReminderDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_DATE = "date";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_TEXT + " TEXT, " +
                    COLUMN_DATE + " TEXT);";

    public ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_TEXT, reminder.getText());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(COLUMN_DATE, dateFormat.format(reminder.getDate()));

        long rowId = -1;

        try {
            rowId = db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return rowId;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder(
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)),
                        parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
                );
                reminder.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reminderList;
    }

    public void deleteReminder(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}