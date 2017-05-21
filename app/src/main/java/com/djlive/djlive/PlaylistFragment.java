package com.djlive.djlive;

        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import static com.djlive.djlive.DJLiveActivity.playlistController;
        import static com.djlive.djlive.DJLiveActivity.typeface;


public class PlaylistFragment extends Fragment  {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflates the recycler view
        RecyclerView view = (RecyclerView) inflater.inflate(
                R.layout.fragment_playlist_session_lists, container, false);

        final PlaylistSessionActivity activity = (PlaylistSessionActivity) getActivity();

        //set the adapter for the users local queue
        FirebaseRecyclerAdapter<Song, PlaylistViewHolder> adapter =
                new FirebaseRecyclerAdapter<Song, PlaylistViewHolder>(
                        Song.class,
                        R.layout.fragment_song_item,
                        PlaylistViewHolder.class,
                        playlistController.refQueue
                ) {
                    @Override
                    protected void populateViewHolder(
                            PlaylistViewHolder localQueueViewHolder, Song song, int position)
                    {
                        localQueueViewHolder.tvArtistName.setText(song.getArtist());
                        localQueueViewHolder.tvSongName.setText(song.getName());
                        localQueueViewHolder.tvAddedBy.setText("Added by " + song.getAddedBy());
                    }
                };

        //attach adapter to recycler view
        view.setAdapter(adapter);

        //sets the recycler view to linear
        view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        view.setLayoutManager(llm);

        return view;
    }

    /**
     *  The view holder contains the views that appear in the user's local queue
     */
    static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvArtistName;
        TextView tvAddedBy;

        PlaylistViewHolder(View v) {
            super(v);

            tvSongName = (TextView) v.findViewById(R.id.tv_song_name);
            tvSongName.setTypeface(typeface);

            tvArtistName = (TextView) v.findViewById(R.id.tv_song_artist);
            tvArtistName.setTypeface(typeface);

            tvAddedBy = (TextView) v.findViewById(R.id.tv_added_by);
            tvAddedBy.setTypeface(typeface);

        }
    }
}

