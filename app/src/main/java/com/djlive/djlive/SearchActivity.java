package com.djlive.djlive;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;
import android.view.MenuItem;


import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;


/**
 * This class implements a search and uses the wrapper classes that was written
 * by Kaaes (github username)
 * Created by Summer on 11/12/2016.
 * @see <a href="https://github.com/kaaes/spotify-web-api-android">
 *       https://github.com/kaaes/spotify-web-api-android</a>
 */

public class SearchActivity extends DJLiveActivity implements Search.View {
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private static final String TAG = SearchActivity.class.getSimpleName();

    private SpotifySearchAdapter searchAdapter;
    private LinearLayoutManager llm;
    private SearchFormatter searchListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        setUpBackButton();

        setUpSearchFormatter();

        setUpSearchView();

        //creates spotify search searchAdapter and adds listener
        setUpSpotifySearchAdapter();

        //inflates the recycler recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_search_results);

        //attach searchAdapter to recycler recyclerView
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
    }

    private void setUpSearchFormatter() {
         searchListener = new SearchFormatter(this, this);
    }

    private void setUpSearchView() {

        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchListener.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    /**
     * Instantiates the searchAdapter and creates a listener for each item in the recyclerView.
     */
    private void setUpSpotifySearchAdapter() {
        searchAdapter = new SpotifySearchAdapter(this, new SpotifySearchAdapter.Listener() {

            /**
             * If list item is selected then return to playlistSession and add
             * item to localqueue and firebase
             * @param track of track selected
             */
            public void onClick(Track track) {
                Song song = null; //just to initialize it. LocalQueue handles null songs anyways

                if (track != null) {
                    song = createSong(track);

                }

                //adds the song to the local queue and updates the user local queue adapter
                user.getLocalQueue().addSong(song);
                userLocalQueueAdapter.notifyDataSetChanged();

                finish();
            }
        });

    }

    /**
     * This class takes a track returned from spotify and creates a new song object
     * @param track from Spotify wrapper class
     * @return new Song object to be passed to localQueue
     */
    private Song createSong(Track track) {
        Song song = null;
        StringBuilder sb = new StringBuilder();
        int count = 1;

        //gets all artist names and creates a string
        for (ArtistSimple artist : track.artists) {

            sb.append(artist.name);

            if (track.artists.size() > 1 && count < track.artists.size()) {
                sb.append(", ");
            }

            count++;
        }

        //creates new song
        if (user != null) {

            song = new Song(track.uri,
                            sb.toString(),
                            track.album.name,
                            user.getUsername(),
                            track.name,
                            track.explicit,
                            track.duration_ms,
                            ((track.album != null &&
                                    track.album.images != null &&
                                    track.album.images.size() > 0)
                                    ? track.album.images.get(0).url
                                    : ""));

        }

        return song;
    }

    @Override
    public void reset() {
        searchAdapter.clearData();
    }


    @Override
    public void addData(List<Track> tracks) {
        searchAdapter.addData(tracks);
    }

    /**
     *  This method adds back button to the action bar that navigates to the
     *  playlistSession activity
     */
    private void setUpBackButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * When the user selects the home button, the user navigates to the playlistSession
     * activity
     * @param item selection menu item
     * @return true if home selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: confirm proper back button functionality
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
