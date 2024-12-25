package com.example.newsapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapplication.R;
import com.example.newsapplication.dao.BookMarkDao;
import com.example.newsapplication.model.Article;
import com.example.newsapplication.model.BookmarkedArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<Article> articles;
    private String loggedInUsername;

    public NewsAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;

        // Get username from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.loggedInUsername = prefs.getString("username", null);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articles.get(position);

        // Title
        holder.tvTitle.setText(article.getTitle() != null ? article.getTitle() : "No Title");

        // Description
        holder.tvDescription.setText(article.getDescription() != null ? article.getDescription() : "No Description");

        // Published At
        holder.tvPublishedAt.setText(article.getPublishedAt() != null ? article.getPublishedAt() : "Unknown Date");

        // Load Image using Picasso
        // If urlToImage is null, Picasso will display a placeholder or do nothing (depending on your config)
        Picasso.get()
                .load(article.getUrlToImage())
                .placeholder(R.drawable.placeholder) // Optional placeholder image
                .error(R.drawable.error_image)       // Optional error image
                .into(holder.ivImage);


        // Handle item click to open article in browser
        holder.itemView.setOnClickListener(v -> {
            if (article.getUrl() != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(browserIntent);
            }
        });


        // Get context to use for DAO
        BookMarkDao bookmarkDao = new BookMarkDao(context);

        // Check if this article is already bookmarked
        boolean isBookmarked = bookmarkDao.isBookmarked(article.getUrl(), loggedInUsername);        if (isBookmarked) {
            holder.ivBookmark.setImageResource(R.drawable.ic_bookmark);
        } else {
            holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);
        }

        // Clicking the bookmark icon toggles bookmark
        holder.ivBookmark.setOnClickListener(v -> {
            if (bookmarkDao.isBookmarked(article.getUrl(), loggedInUsername)) {
                // Remove bookmark
                bookmarkDao.deleteBookmark(article.getUrl() , loggedInUsername);
                holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);
            } else {
                // Add bookmark
                // Convert from Article to BookmarkedArticle
                BookmarkedArticle ba = new BookmarkedArticle(
                        article.getUrl(),
                        article.getTitle(),
                        article.getDescription(),
                        article.getUrlToImage(),
                        loggedInUsername
                );
                bookmarkDao.addBookmark(ba);
                holder.ivBookmark.setImageResource(R.drawable.ic_bookmark);
            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPublishedAt;
        ImageView ivImage, ivBookmark;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPublishedAt = itemView.findViewById(R.id.tvPublishedAt);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }
    }
}