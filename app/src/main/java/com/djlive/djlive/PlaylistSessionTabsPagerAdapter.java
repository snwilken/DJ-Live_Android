package com.djlive.djlive;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * The PlaylistSessionTabsPagerAdapter sets up the tab count, fragments, and tab titles
 * @author Sums
 **/

public class PlaylistSessionTabsPagerAdapter extends FragmentPagerAdapter {

    //instance variables
    private User user;
    private Context context;
    private final List<Fragment> fragments = new ArrayList<>();

    /**
     * The constructor which instantiates the user and context instance variable
     * @param fm FragmentManager
     */
    PlaylistSessionTabsPagerAdapter(FragmentManager fm, Context context, User user) {
        super(fm);
        this.context = context;
    }

    /**
     * Sets the number of pages that are in the pager adapter
     * @return number of pages
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * Returns the user local queue, spotify search, playlist, and contributing user fragments
     * @param position of fragment
     * @return local queue, search, playlist or contributing user fragment
     */
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    /**
     * Adds fragments to the fragment arraylist
     */
    void addFragment(Fragment fragment) {

        fragments.add(fragment);
    }
}
