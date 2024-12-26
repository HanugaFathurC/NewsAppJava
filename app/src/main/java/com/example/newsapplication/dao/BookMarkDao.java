package com.example.newsapplication.dao;

import android.content.Context;


import com.example.newsapplication.model.BookmarkedArticle;
import com.example.newsapplication.util.DatabaseHelper;

import java.util.List;

public class BookMarkDao {
    private final DatabaseHelper dbHelper;

    public BookMarkDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add a bookmark
    public boolean addBookmark(BookmarkedArticle article) {
        return dbHelper.addBookmark(article.getTitle(), article.getDescription(), article.getUrl(),
                article.getImageUrl(), article.getUsername());
    }

    // Delete a bookmark
    public boolean deleteBookmark(String url, String username) {
        return dbHelper.deleteBookmark(url, username);
    }

    // Check if a URL is already bookmarked
    public boolean isBookmarked(String url, String username) {
        return dbHelper.isBookmarked(url, username);
    }

    // Get all bookmarks by username
    public List<BookmarkedArticle> getBookmarksByUsername(String username) {
        return dbHelper.getBookmarksByUsername(username);
    }

}
