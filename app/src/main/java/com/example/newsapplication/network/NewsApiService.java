package com.example.newsapplication.network;

import com.example.newsapplication.model.NewsApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NewsApiService {

    /**
     * Get top headlines from a specific country
     * @param country The country code (e.g. us, gb, etc.)
     * @param apiKey The API key
     * @return A {@link Call} object that contains the response
     */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "User-Agent: Mozilla/5.0"
    })
    @GET("top-headlines")
    Call<NewsApiResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    /**
     * Get news articles based on a query
     * @param query The search query
     * @param apiKey The API key
     * @return A {@link Call} object that contains the response
     */

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "User-Agent: Mozilla/5.0"
    })
    @GET("everything")
    Call<NewsApiResponse> searchNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );

    /**
     * Get news articles based on a category
     * @param category The category
     * @param country The country code (e.g. us, gb, etc.)
     * @param apiKey The API key
     * @return A {@link Call} object that contains the response
     */
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "User-Agent: Mozilla/5.0"
    })
    @GET("top-headlines")
    Call<NewsApiResponse> getNewsByCategory(
            @Query("category") String category,
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

}
