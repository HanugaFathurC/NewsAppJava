package com.example.newsapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newsapplication.R;
import com.example.newsapplication.adapter.BookmarkAdapter;
import com.example.newsapplication.dao.BookMarkDao;
import com.example.newsapplication.model.BookmarkedArticle;

import java.util.ArrayList;
import java.util.List;


public class BookmarkedFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private List<BookmarkedArticle> bookmarkedArticles = new ArrayList<>();
    private List<BookmarkedArticle> filteredList = new ArrayList<>();
    private TextView tvEmptyMessage;
    private String loggedInUsername;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarked, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBookmark);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookmarkAdapter = new BookmarkAdapter(getContext(), filteredList, this::handleBookmarkAllDeleted);
        recyclerView.setAdapter(bookmarkAdapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        loggedInUsername = prefs.getString("username", null);

        if (loggedInUsername != null) {
            loadBookmarks();
        } else {
            bookmarkedArticles.clear();
            filteredList.clear();
            updateEmptyState();
        }


        setHasOptionsMenu(true);

        return view;
    }

    private void loadBookmarks() {
        if (loggedInUsername == null) return;

        // Load bookmarks specific to the logged-in user
        BookMarkDao dao = new BookMarkDao(getContext());
        bookmarkedArticles.clear();
        bookmarkedArticles.addAll(dao.getBookmarksByUsername(loggedInUsername));
        filteredList.clear();
        filteredList.addAll(bookmarkedArticles);

        // Show/hide the empty state message
        updateEmptyState();

        bookmarkAdapter.notifyDataSetChanged();
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bookmark_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search bookmarks...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBookmarks(query); // Perform filtering
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBookmarks(newText); // Perform real-time filtering
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void filterBookmarks(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(bookmarkedArticles);
        } else {
            for (BookmarkedArticle article : bookmarkedArticles) {
                if (article.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        (article.getDescription() != null && article.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(article);
                }
            }
        }
        updateEmptyState();
        bookmarkAdapter.notifyDataSetChanged();
    }

    private void handleBookmarkAllDeleted(){
        loadBookmarks();
    }
}