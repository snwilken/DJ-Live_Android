package com.djlive.djlive;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Summer on 11/13/2016.
 */

public class Search {

    public interface View {
        void reset();

        void addData(List<Track> items);
    }
}
