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

import kaaes.spotify.webapi.android.models.Image;

import static com.djlive.djlive.DJLiveActivity.playlistController;
import static com.djlive.djlive.DJLiveActivity.typeface;

/**
 * This fragment stores the list of users that are currently
 * joined to the playlist.
 */
public class ContributingUsersFragment extends Fragment  {


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

        //set the adapter for the users local queue
        FirebaseRecyclerAdapter<String, UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, UserViewHolder>(
                        String.class,
                        R.layout.fragment_contributing_user,
                        UserViewHolder.class,
                        playlistController.refUsers
                ) {
                    @Override
                    protected void populateViewHolder(
                            UserViewHolder UserViewHolder, String s, int position)
                    {
                        UserViewHolder.tvUserName.setText(s);

                        switch (s.substring(0,1)) {
                            case "a":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_a);
                                break;
                            case "b":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_b);
                                break;
                            case "c":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_c);
                                break;
                            case "d":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_d);
                                break;
                            case "e":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_e);
                                break;
                            case "f":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_f);
                                break;
                            case "g":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_g);
                                break;
                            case "h":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_h);
                                break;
                            case "i":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_i);
                                break;
                            case "j":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_j);
                                break;
                            case "k":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_k);
                                break;
                            case "l":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_l);
                                break;
                            case "m":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_m);
                                break;
                            case "n":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_n);
                                break;
                            case "o":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_o);
                                break;
                            case "p":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_p);
                                break;
                            case "q":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_q);
                                break;
                            case "r":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_r);
                                break;
                            case "s":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_s);
                                break;
                            case "t":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_t);
                                break;
                            case "u":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_u);
                                break;
                            case "v":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_v);
                                break;
                            case "w":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_w);
                                break;
                            case "x":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_x);
                                break;
                            case "y":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_y);
                                break;
                            case "z":
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_z);
                                break;
                            default:
                                UserViewHolder.ivUser.setImageResource(R.drawable.letter_android);
                                break;


                        }
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
    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        ImageView ivUser;

        UserViewHolder(View v) {
            super(v);

            tvUserName = (TextView) v.findViewById(R.id.tv_userName);
            tvUserName.setTypeface(typeface);
            ivUser     = (ImageView) v.findViewById(R.id.iv_contributing_user);

        }
    }
}

