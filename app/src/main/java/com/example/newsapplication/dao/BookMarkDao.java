package com.example.newsapplication.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.newsapplication.model.BookmarkedArticle;
import com.example.newsapplication.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookMarkDao {
    private DatabaseHelper dbHelper;

    public BookMarkDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add a bookmark
    public boolean addBookmark(BookmarkedArticle article) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_URL, article.getUrl());
        values.put(DatabaseHelper.COLUMN_TITLE, article.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, article.getDescription());
        values.put(DatabaseHelper.COLUMN_IMAGE_URL, article.getImageUrl());
        values.put(DatabaseHelper.COLUMN_BOOKMARK_USERNAME, article.getUsername());

        long result = db.insert(DatabaseHelper.TABLE_BOOKMARKS, null, values);
        db.close();
        return result != -1;
    }

    // Delete a bookmark
    public boolean deleteBookmark(String url, String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_BOOKMARKS,
                DatabaseHelper.COLUMN_URL + "=? AND " + DatabaseHelper.COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{url, username});
        db.close();
        return rows > 0;
    }
    // Check if a URL is already bookmarked
    public boolean isBookmarked(String url, String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKMARKS,
                new String[]{DatabaseHelper.COLUMN_URL},
                DatabaseHelper.COLUMN_URL + "=? AND " + DatabaseHelper.COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{url, username}, null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    // Get all bookmarks
    public List<BookmarkedArticle> getAllBookmarks() {
        List<BookmarkedArticle> bookmarkList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKMARKS,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URL));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKMARK_USERNAME));

                bookmarkList.add(new BookmarkedArticle(url, title, description, imageUrl, username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookmarkList;
    }

    public List<BookmarkedArticle> getBookmarksByUsername(String username) {
        List<BookmarkedArticle> bookmarkList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKMARKS,
                null, DatabaseHelper.COLUMN_BOOKMARK_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URL));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));

                bookmarkList.add(new BookmarkedArticle(url, title, description, imageUrl, username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookmarkList;
    }

}
