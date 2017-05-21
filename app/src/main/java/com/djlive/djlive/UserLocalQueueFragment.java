package com.djlive.djlive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.djlive.djlive.DJLiveActivity.userLocalQueueAdapter;

public class UserLocalQueueFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //inflates the recycler view
        RecyclerView view = (RecyclerView) inflater.inflate(
                R.layout.fragment_playlist_session_lists, container, false);

        //attach adapter to recycler view
        view.setAdapter(userLocalQueueAdapter);
        view.setHasFixedSize(true);

        //sets the recycler view to linear
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        view.setLayoutManager(llm);

        return view;
    }
}