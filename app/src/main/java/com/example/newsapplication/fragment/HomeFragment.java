package com.example.newsapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;


import com.example.newsapplication.R;
import com.example.newsapplication.adapter.CategoryAdapter;
import com.example.newsapplication.adapter.NewsAdapter;
import com.example.newsapplication.model.Article;
import com.example.newsapplication.model.NewsApiResponse;
import com.example.newsapplication.network.NewsApiService;
import com.example.newsapplication.network.RetrofitClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private String NEWS_API_KEY;
    private static final String COUNTRY = "us";
    private static final String DEFAULT_CATEGORY = "general";
    private NewsAdapter newsAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Article> articleList = new ArrayList<>();
    private RecyclerView categoryRecyclerView;
    private RecyclerView newsRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NEWS_API_KEY = getString(R.string.news_api_key);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        newsRecyclerView = view.findViewById(R.id.recyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the recycler view
        setupCategoryRecyclerView();
        setupNewsRecyclerView();

        // Fetch news from the API using Retrofit
        fetchNews(DEFAULT_CATEGORY);

        return view;
    }

    /**
     * Set up buttons for the different news categories on category recycler view.
     */
    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList("General", "Business", "Technology", "Entertainment", "Sports", "Health", "Science");

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getContext(), categories, this::fetchNews);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }


    /**
     * Set up content for the different news categories.
     */
    private void setupNewsRecyclerView() {
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext(), articleList);
        newsRecyclerView.setAdapter(newsAdapter);
    }



    /**
     * Fetch news based on the selected category.
     */
    private void fetchNews(String category) {
        NewsApiService service = RetrofitClient.getRetrofitInstance()
                .create(NewsApiService.class);

        Call<NewsApiResponse> call = service.getNewsByCategory(category, COUNTRY, NEWS_API_KEY);
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> fetchedArticles = response.body().getArticles();
                    // Filter out articles if needed (optional)
                    List<Article> validArticles = filterRemoved(fetchedArticles);

                    articleList.clear();
                    articleList.addAll(validArticles);
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setBackgroundColor(getResources().getColor(R.color.teal));
        searchView.setQueryHint("Search news...");

        int searchAutoCompleteId = androidx.appcompat.R.id.search_src_text;
        TextView searchAutoComplete = searchView.findViewById(searchAutoCompleteId);
        if (searchAutoComplete != null) {
            // Set text color to white
            searchAutoComplete.setTextColor(getResources().getColor(android.R.color.white));
            // Set hint text color to white
            searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        }

        // Listen for query submissions
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the search
                performSearch(query);
                searchView.clearFocus(); // Hide keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: do real-time search as user types (not recommended without debounce)
                return false;
            }
        });

    }

    /**
     * Search news using the "everything" endpoint with the user's query.
     */
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            fetchNews(DEFAULT_CATEGORY);
            return;
        }

        NewsApiService service = RetrofitClient.getRetrofitInstance()
                .create(NewsApiService.class);

        Call<NewsApiResponse> call = service.searchNews(query, NEWS_API_KEY);
        call.enqueue(new Callback<NewsApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> fetchedArticles = response.body().getArticles();
                    List<Article> validArticles = filterRemoved(fetchedArticles);

                    articleList.clear();
                    articleList.addAll(validArticles);
                    newsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Optional: Filter out articles that are marked as "[Removed]".
     */
    private List<Article> filterRemoved(List<Article> fetchedArticles) {
        List<Article> validArticles = new ArrayList<>();
        for (Article article : fetchedArticles) {
            if (!isRemoved(article)) {
                validArticles.add(article);
            }
        }
        return validArticles;
    }

    /**
     * Checks if an article has "[Removed]" in source name, title, or description.
     */
    private boolean isRemoved(Article article) {
        if (article.getTitle() != null && article.getTitle().equals("[Removed]")) {
            return true;
        }
        if (article.getDescription() != null && article.getDescription().equals("[Removed]")) {
            return true;
        }
        if (article.getSource() != null && article.getSource().getName() != null) {
            return article.getSource().getName().equals("[Removed]");
        }
        return false;
    }

}