package com.example.newsapplication.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.newsapplication.model.BookmarkedArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private Context context;
    private List<BookmarkedArticle> bookmarkList;
    private String loggedInUsername;
    private Runnable onBookmarkDeletedAll;

    public BookmarkAdapter(Context context, List<BookmarkedArticle> bookmarkList, Runnable onBookmarkDeletedAll) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.onBookmarkDeletedAll = onBookmarkDeletedAll;

        // Get username from SharedPreferences
        this.loggedInUsername = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("username", null);

    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmarked, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        BookmarkedArticle article = bookmarkList.get(position);

        holder.tvTitle.setText(article.getTitle());
        holder.tvDescription.setText(article.getDescription());
        Picasso.get().load(article.getImageUrl()).into(holder.ivImage);

        // Open the article in a browser when clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            BookMarkDao dao = new BookMarkDao(context);
            dao.deleteBookmark(article.getUrl(), loggedInUsername);
            // Remove from the local list
            bookmarkList.remove(position);
            notifyItemRemoved(position);


            if (onBookmarkDeletedAll != null) {
                onBookmarkDeletedAll.run();
            }

        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivImage, ivDelete;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookmarkTitle);
            tvDescription = itemView.findViewById(R.id.tvBookmarkDesc);
            ivImage = itemView.findViewById(R.id.ivBookmarkImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}