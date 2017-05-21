package com.djlive.djlive;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

/**
 * This class implements a search and uses the wrapper class that was written
 * by Kaaes (github username)
 * Created by Summer on 11/12/2016
 * @see <a href="https://github.com/kaaes/spotify-web-api-android">Spotify Web Api Wrapper</a>
 */

class SearchFormatter {
    //constants
    private static final String TAG = SearchFormatter.class.getSimpleName();
    private static final int PAGE_SIZE = 50;

    //instance variables
    private int currentOffset;
    private String currentQuery;
    private Context context;
    private Search.View view;
    private SpotifyApi spotifyApi;
    private SpotifyAPIResponseListener spotifyAPIResponseListener;

    SearchFormatter(Context context, Search.View view) {
        this.context = context;
        this.view = view;
        this.spotifyApi = new SpotifyApi();
    }

    interface SpotifyAPIResponseListener {
        void onComplete(List<Track> track);
        void onError(SpotifyError error);
    }

    void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(currentQuery)) {
            currentQuery = searchQuery;
            view.reset();
            spotifyAPIResponseListener = new SpotifyAPIResponseListener() {
                @Override
                public void onComplete(List<Track> tracks) {
                    view.addData(tracks);
                }

                @Override
                public void onError(SpotifyError error) {

                }
            };
            getFirstPage(searchQuery, spotifyAPIResponseListener);
        }
    }

    private void getFirstPage(String query, SpotifyAPIResponseListener spotifyAPIResponseListener) {
        this.currentOffset = 0;
        currentQuery = query;
        getData(query, 0, PAGE_SIZE, spotifyAPIResponseListener);
    }

    private void loadMoreResults() {
        Log.d(TAG, "Load more...");
        getNextPage(spotifyAPIResponseListener);
    }

    private void getNextPage(SpotifyAPIResponseListener spotifyAPIResponseListener) {
        this.currentOffset += PAGE_SIZE;
        getData(currentQuery, currentOffset, PAGE_SIZE, spotifyAPIResponseListener);
    }

    private void getData(String query, int offset, final int limit, final SpotifyAPIResponseListener spotifyAPIResponseListener) {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        this.spotifyApi.getService().searchTracks(query, options, new SpotifyCallback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                spotifyAPIResponseListener.onComplete(tracksPager.tracks.items);
            }

            @Override
            public void failure(SpotifyError error) {
                spotifyAPIResponseListener.onError(error);
            }
        });
    }
}
