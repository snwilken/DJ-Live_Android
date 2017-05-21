package com.djlive.djlive;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

import static com.djlive.djlive.DJLiveActivity.typeface;
import static com.djlive.djlive.DJLiveActivity.user;

/**
 * The class is the adapter to the recycler view for the search activity
 * This class utilizes the spotify-web-api wrapper classes created by kasia (github username: kaaes)
 * @see <a href="https://github.com/kaaes/spotify-web-api-android">
 *     https://github.com/kaaes/spotify-web-api-android</a>
 * Created by Summer on 10/26/2016.
 */

class SpotifySearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //instance variables
    private static final String TAG = SpotifySearchAdapter.class.getSimpleName();
    private final List<Track> tracks = new ArrayList<>();
    private final Context context;
    private Listener listener; //listener put on each song item

    /**
     *  The view holder contains the views that appear in our playlist
     */
    static class PermittedViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView artist;
        final ImageView image;
        final Button add;

        PermittedViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.tv_song_name);
            title.setTypeface(typeface);

            artist = (TextView) v.findViewById(R.id.tv_song_subtitle);
            artist.setTypeface(typeface);

            image = (ImageView) v.findViewById(R.id.iv_search_album);

            add = (Button) v.findViewById(R.id.btnAddSong);
        }
    }

    /**
     *  The view holder contains the views that appear in our playlist
     */
    static class BlockedViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView artist;
        final TextView banned;
        final ImageView image;
        final Button blocked;

        BlockedViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.tv_song_name);
            title.setTypeface(typeface);

            artist = (TextView) v.findViewById(R.id.tv_song_subtitle);
            artist.setTypeface(typeface);

            image = (ImageView) v.findViewById(R.id.iv_search_album);

            blocked = (Button) v.findViewById(R.id.btnBlock);

            banned = (TextView) v.findViewById(R.id.tv_song_banned);
            banned.setTypeface(typeface);
        }
    }

    /**
     * Interface that adds onClick event to each search list item
     */
    interface Listener {
        void onClick(Track track);
    }

    /**
     * Registers listener
     * @param listener handles list item onClick event
     */
    void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Constructor for search adapter
     * @param context app context
     * @param listener handles list item onClick event
     */
    SpotifySearchAdapter(Context context, Listener listener) {
        this.context  = context;
        this.listener = listener;
    }

    /**
     * Returns the which item view should be returned based on whether the track
     * validates the host's playlist settings
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {

        Track track = tracks.get(position);

        return isValid(track) ? 0 : 1;
    }
    /**
     * Inflates the fragment_search_song_item view
     * @param parent recycler view
     * @param viewType
     * @return viewholder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view0 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_song_item, parent, false);

        View view1 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song_item_banned, parent, false);


        switch (viewType) {
            case 0: return new SpotifySearchAdapter.PermittedViewHolder(view0);
            case 1: return new SpotifySearchAdapter.BlockedViewHolder(view1);
        }

        return null;
    }


    /**
     * Binds the song title, album art, and subtitle to the fragment_search_song_item
     * @param holder stores the view
     * @param position stores the position of the titles, subtitles, and images in each dataset
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Track track = tracks.get(position);

        if (holder == null) {
            return;
        }

        switch (holder.getItemViewType()) {
            case 0:
                PermittedViewHolder vhpermitted = (PermittedViewHolder)holder;

                vhpermitted.title.setText(track.name);

                vhpermitted.artist.setText(setArtistName(track));

                setAlbumImage(vhpermitted, track);

                vhpermitted.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onClick(track);
                            //when the cardview is clicked call the SpotifyAPIResponseListener onClick()
                        }
                    }
                });
                break;

            case 1:

                BlockedViewHolder vhBlocked = (BlockedViewHolder)holder;

                vhBlocked.title.setText(track.name);

                vhBlocked.banned.setText("Blocked");

                vhBlocked.artist.setText(setArtistName(track));

                setAlbumImage(vhBlocked, track);

                vhBlocked.banned.setOnClickListener(null);

                break;
        }
    }


    boolean isValid(Track track) {

        return user.getLocalQueue().validateTrack(track);

    }

    private void setAlbumImage(RecyclerView.ViewHolder vh, Track track) {

        BlockedViewHolder bvh = null;
        PermittedViewHolder pvh = null;

        if (vh instanceof BlockedViewHolder) {
            bvh = (BlockedViewHolder) vh;
        } else {
            pvh = (PermittedViewHolder) vh;
        }

        if (track.album != null && track.album.images != null && (!track.album.images.isEmpty())) {
            Image albumArt = track.album.images.get(0);
            Picasso.with(context).load(albumArt.url).into(
                    vh instanceof BlockedViewHolder
                            ? bvh.image
                            : pvh.image);
        } else {
            int id = context.getResources().getIdentifier("note", "drawable",
                    context.getPackageName());

            if (vh instanceof BlockedViewHolder) {
                bvh.image.setImageResource(id);
            } else {
                pvh.image.setImageResource(id);
            }
        }
    }

    /**
     * Sets artist track
     * @param track
     */
    private String setArtistName(Track track) {
        StringBuilder sb = new StringBuilder();
        int count = 1;

        //gets all artist names
        for (ArtistSimple artist : track.artists) {
            sb.append(artist.name);

            if (track.artists.size() > 1 && count < track.artists.size()) {
                sb.append(", ");
            }

            count++;
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() { return tracks.size(); }

    /**
     * This method clears the arraylist of tracks
     */
    void clearData() { tracks.clear(); }

    /**
     * This method adds an arraylist of tracks
     * @param tracks list of tracks returned from the query
     */
    void addData(List<Track> tracks) {
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }
}