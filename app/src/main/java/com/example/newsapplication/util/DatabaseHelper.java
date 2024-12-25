package com.example.newsapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.newsapplication.model.BookmarkedArticle;
import com.example.newsapplication.model.User;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NewsApp.db";
    private static final int DATABASE_VERSION = 5;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PROFILE_PICTURE = "profile_picture";

    // Bookmarks table
    public static final String TABLE_BOOKMARKS = "bookmarks";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_BOOKMARK_USERNAME = "bookmark_username";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_FULL_NAME + " TEXT, " +
                COLUMN_BIRTH_DATE + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PROFILE_PICTURE + " TEXT)";
        db.execSQL(createUserTable);

        String createBookmarksTable = "CREATE TABLE " + TABLE_BOOKMARKS + " (" +
                COLUMN_URL + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_BOOKMARK_USERNAME + " TEXT)";
        db.execSQL(createBookmarksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS bookmarks");
        onCreate(db); //
    }

    // ========= USERS TABLE METHODS =========

    // Method to register a user
    public boolean registerUser(String username, String fullName, String birthDate, String gender, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_BIRTH_DATE, birthDate);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // Return true if insertion is successful
    }

    // Method to login a user
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password});
        return cursor.getCount() > 0;
    }


    public boolean updateUserProfile(String username, String fullName, String gender, String dateOfBirth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_BIRTH_DATE, dateOfBirth);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsUpdated > 0; // Return true if at least one row is updated
    }

    // Method to get user by username
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, full_name, gender, birth_date, password FROM users WHERE username = ?", new String[]{username});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
            user.setBirthDate(cursor.getString(cursor.getColumnIndexOrThrow("birth_date")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        }
        cursor.close();
        return user; // null if user doesn't exist
    }

    public String getProfilePicture(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE_PICTURE + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});

        String profilePicturePath = null;
        if (cursor.moveToFirst()) {
            profilePicturePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PICTURE));
        }
        cursor.close();
        return profilePicturePath;
    }

    public boolean updateProfilePicture(String username, String profilePicturePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PICTURE, profilePicturePath);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsUpdated > 0;
    }

    public boolean updatePassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsUpdated > 0;
    }

    // ========= BOOKMARKS TABLE METHODS =========
    // Add a bookmark
    public boolean addBookmark(String title, String description, String url, String imageUrl, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_BOOKMARK_USERNAME, username);

        long result = db.insert(TABLE_BOOKMARKS, null, values);
        return result != -1;
    }

    public List<BookmarkedArticle> getBookmarksByUsername(String username) {
        List<BookmarkedArticle> bookmarks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL));

                bookmarks.add(new BookmarkedArticle(url, title, description, imageUrl, username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookmarks;
    }

    // Delete a bookmark by URL
    public boolean deleteBookmark(String url, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_BOOKMARKS,
                COLUMN_URL + "=? AND " + COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{url, username});
        db.close();
        return rows > 0;
    }

    // Check if an article is already bookmarked
    public boolean isBookmarked(String url, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARKS,
                new String[]{COLUMN_URL},
                COLUMN_URL + "=? AND " + COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{url, username}, null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

}
