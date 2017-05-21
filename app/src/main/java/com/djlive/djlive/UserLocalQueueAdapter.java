package com.djlive.djlive;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static com.djlive.djlive.DJLiveActivity.typeface;

public class UserLocalQueueAdapter extends RecyclerView.Adapter<UserLocalQueueAdapter.ViewHolder> {

    private ArrayList<Song> localQueue;

    /**
     *  The view holder contains the views that appear in the user's local queue
     **/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvArtistName;
        TextView tvAddedBy;

        ViewHolder(View v) {
            super(v);

            tvSongName = (TextView) v.findViewById(R.id.tv_song_name);
            tvSongName.setTypeface(typeface);

            tvArtistName = (TextView) v.findViewById(R.id.tv_song_artist);
            tvArtistName.setTypeface(typeface);

            tvAddedBy = (TextView) v.findViewById(R.id.tv_added_by);
            tvAddedBy.setTypeface(typeface);

        }
    }

    /**
     * This constructor will assign the user's local queue. It currently populates songNames and
     * songTitles with dummy data for display.
     * @param localQueue the actual LocalQueue object in the User.
     **/
    UserLocalQueueAdapter(LocalQueue localQueue) {
        this.localQueue = localQueue.getLocalQueue();
    }

    /**
     * Inflates the song_item view
     * @param parent recycler view
     * @param viewType
     * @return viewholder
     **/
    @Override
    public UserLocalQueueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new UserLocalQueueAdapter.ViewHolder(v);
    }

    /**
     * Binds the song title and artist to the fragment_song_item
     * @param holder stores the view
     * @param position stores the position of the names and titles in each dataset
     */
    @Override
    public void onBindViewHolder(UserLocalQueueAdapter.ViewHolder holder, int position) {

        holder.tvSongName.setText(localQueue.get(position).getName());
        holder.tvArtistName.setText(localQueue.get(position).getArtist());
        holder.tvAddedBy.setText("Added by " + localQueue.get(position).getAddedBy());

    }

    /**
     * Returns the number of localQueue
     * @return the # of items (localQueue)
     **/
    @Override
    public int getItemCount() {

        //return playlistQueue.size();
        return localQueue.size();
    }
}