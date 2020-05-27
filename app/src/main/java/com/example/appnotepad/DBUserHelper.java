package com.example.appnotepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBUserHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "User";

    // Table name: User.
    private static final String TABLE_USER = "Password";

    private static final String COLUMN_PASS = "pass";

    public DBUserHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.
        String script = "CREATE TABLE " + TABLE_USER
                + "(" +
                COLUMN_PASS + " INTEGER PRIMARY KEY" +
                ")";
        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }


    public void addUser(User user) {
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + user.getPass());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASS, user.getPass());

        // Inserting Row
        db.insert(TABLE_USER, null, values);

        // Closing database connection
        db.close();
    }


    public User getUser(int id) {
        Log.i(TAG, "MyDatabaseHelper.getPass ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_PASS}, COLUMN_PASS + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(cursor.getString(0));
        // return user
        return user;
    }


    public List<User> getAllUsers() {
        Log.i(TAG, "MyDatabaseHelper.getAllUsers ... ");

        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setPass(cursor.getString(0));

                // Adding user to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return user list
        return userList;
    }

    public int getUsersCount() {
        Log.i(TAG, "MyDatabaseHelper.getUsersCount ... ");

        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }


    public int updateUser(User user) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + user.getPass());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASS, user.getPass());

        // updating row
        return db.update(TABLE_USER, values, COLUMN_PASS + " = ?",
                new String[]{String.valueOf(user.getPass())});
    }

    public void deleteUser(User user) {
        Log.i(TAG, "MyDatabaseHelper.updateUser ... " + user.getPass());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_PASS + " = ?",
                new String[]{String.valueOf(user.getPass())});
        db.close();
    }

}
