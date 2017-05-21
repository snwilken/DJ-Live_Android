package com.djlive.djlive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

import static com.djlive.djlive.R.id.tbl_playlist_session;
import static com.djlive.djlive.R.id.vp_playlist_session;

/**
 * The playlist session activity is where most of the DJ Live application functionality takes place.
 * It contains the following:
 * <ul>
 *     <ol>Playlist Fragment - displays songs in our communal playlist</ol>
 *     <ol>Contributing Users Fragment - displays users currently joined to the playlist</ol>
 *     <ol>Local Queue Fragment - displays all of the songs in the user's local queue</ol>
 * </ul>
 * @see <a href=https://github.com/spotify/android-sdk>Spotify Android SDK</a>
 **/

public class PlaylistSessionActivity extends DJLiveActivity implements
        SpotifyPlayer.NotificationCallback,
        UIUpdateListener {
    //constants
    public static final String AUTH_TOKEN = "auth_token";
    private static final String TAG = PlaylistSessionActivity.class.getSimpleName();

    //instance variables
    private TabLayout tabs;  //layout that stores tabs
    private ViewPager pager; //allows for tabbing between listviews
    private PlaylistSessionTabsPagerAdapter pagerAdapter; //adds tabs and binds views to tabs
    private Context context;
    private Button btnPlayPause; //buttons in player ui
    private Button btnSkip;
    private DrawerLayout dlPlaylistSession; //drawer layout for navigation drawer that stores settings
    private NavigationView nvPlaylistSession; //navigation view that encapsulates the playlist
                                             // session view
    private ImageView ivAlbumArt;
    private TextView  tvSongName;
    private Bitmap bitmap;
    private ColorDrawable colorDrawable;

    //tab icons
    final int[] ICONS = new int[]{
            R.drawable.ic_playlist_play_white_24dp,
            R.drawable.ic_list_white_24dp,
            R.drawable.ic_people_white_24dp};

    /**
     * Binds view and sets up pageview and tabs
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        this.colorDrawable = new ColorDrawable(); //to change color of app bar

        //sets up main view
        setUpLayout();

        //sets up view pager and pager adapter for tabbed activity
        setUpPagerAndAdapter();

        //add icons to tabs
        setUpTabs();

        //initialize nav drawer
        setUpNavigationDrawer();

        //add host settings dynamically to navigation drawer
        addSettingsToNavigationDrawer();

        //sets up hamburger bar that leads to navigation drawer
        setUpHamburgerBar();

        //setup logout listener for guests
        setUpGuestLogoutListener();

        //makes sure the playlists are created before setting Firebase listeners
        playlistController.setupRefListeners();

        //Two things can update the controls and album UI
        //1: The playlist controller when it sees a new song is at the top of the queue
        playlistController.setUIUpdateListener(this);

        //2: The DJLive activity that manages the player callbacks
        setUIUpdateListener(this);
    }

    /**
     * Inflates the menu in the app bar
     * @param menu app bar menu
     * @return true on completion
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets up the view pager from the playlist session within the xml and instantiates the
     * pager adapter with support fragment manager, context, and user object
     */
    private void setUpPagerAndAdapter() {
        this.pager = (ViewPager) findViewById(vp_playlist_session);
        this.pagerAdapter = new PlaylistSessionTabsPagerAdapter(
                this.getSupportFragmentManager(), context, user);

        setupViewPager(pager);
    }

    /**
     * Sets up content view with either the host playlist session layout that includes
     * player ui or playlist session layout without any player ui
     */
    private void setUpLayout() {
        //if user is a guest then set-up playlist session layout with player buttons
        setContentView(R.layout.activity_playlist_session);

        //if user is a host then set-up playlist session layout that includes player buttons
        if (user.isUserHost()) {
            this.btnPlayPause = (Button) findViewById(R.id.btnPlay);
            this.btnSkip = (Button) findViewById(R.id.btnSkip);
        } else {
            //hide the playback controls if they are a Guest
            View playerControls = findViewById(R.id.fragment_player);
            playerControls.setVisibility(View.GONE);
        }

        this.ivAlbumArt = (ImageView) findViewById(R.id.iv_album_art);
        this.tvSongName = (TextView) findViewById(R.id.tv_song_name);
        tvSongName.setTypeface(typeface);
    }

    /**
     * If search is selected then navigate to search activity or if hamburger button is
     * selected then open navigation drawer
     * @param item selected menu item is either search or home
     * @return true after successful completion
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handles item selection
        switch (item.getItemId()) {
            case R.id.search:
                Intent data = new Intent(this, SearchActivity.class);
                startActivityForResult(data, 1); //goes to activity for result
                break;

            //opens navigation drawer
            case android.R.id.home:
                //Push navigation to x-axis position at the start of its container,
                // not changing its size per Android documentation
                dlPlaylistSession.openDrawer(GravityCompat.START);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Dynamically adds playlist settings to navigation drawer
     */
    private void addSettingsToNavigationDrawer() {
        Menu menu = this.nvPlaylistSession.getMenu();
        Settings settings = user.getPlaylist().getSettings();
        int msPerMinute = 60000;

        menu.add(getString(R.string.settings_playlist_name) + settings.getName());

        menu.add((settings.getAllowExplicit()) ? getString(R.string.settings_allow_explicit)
                : getString(R.string.settings_doesnt_allow_explicit));

        menu.add(getString(R.string.settings_playlist_pin) + settings.getPin());

        menu.add(getString(R.string.settings_max_number_of_guests) + settings.getMaxGuests());

        menu.add(getString(R.string.settings_max_song_length) + (settings.getMaxLength() /
                msPerMinute) + (getString(R.string.settings_minutes)));

        menu.add(getString(R.string.settings_songs_per_guest) + settings.getSongsPerUser());

        menu.add("Host: " + getHostUsername());
    }

    /**
     *  Binds tablayout, adds icons to tabs, and binds tabs to viewpager
     **/
    private void setUpTabs() {

        this.tabs  = (TabLayout) findViewById(tbl_playlist_session);
        tabs.setupWithViewPager(pager);
        addTabIcons();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     *  This method adds hamburger bar to the action bar that opens navigation drawer
     */
    private void setUpHamburgerBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Adds fragments to pageadapter
     * @param pager the view pager binds the playlist, local queue, and contributing user fragments
     *              to view
     */
    private void setupViewPager(ViewPager pager) {

        //adds fragments for the viewpager to switch between
        pagerAdapter.addFragment(new PlaylistFragment());
        pagerAdapter.addFragment(new UserLocalQueueFragment());
        pagerAdapter.addFragment(new ContributingUsersFragment());

        //Set a PagerAdapter supplies views for this pager as needed.
        pager.setAdapter(pagerAdapter);
    }

    /**
     * Adds playlist, local queue, and users icons to the to the tabs
     **/
    private void addTabIcons() {
        //adds icon to tabs
        for (int i = 0; i < ICONS.length; i++) {

            if (tabs.getTabAt(i) != null) {
                tabs.getTabAt(i).setIcon(ICONS[i]);
            }
        }
    }


    @Override
    public void updateControlsUI() {
        //if music is currently playing then update show pause button
        //otherwise play should be displayed
        if (user.isUserHost()) {
            if (player.getPlaybackState().isPlaying) {
                this.btnPlayPause.setBackgroundResource(R.drawable.ic_pause_white_24dp);
            } else {
                this.btnPlayPause.setBackgroundResource(R.drawable.ic_play_arrow_white_24dp);
            }

            //if there are no songs in the user's playlist then disable the play and skip
            //functionality
            this.btnPlayPause.setEnabled(!user.getPlaylist().isEmpty());
            this.btnSkip.setEnabled(!user.getPlaylist().isEmpty());
        }
    }

    @Override
    public void updateAlbumUI() {
        //if the list isnt empty, set the colors to the album are of the first song
        if(user.getPlaylist().getFirstSong() != null) {
            String imageURL = user.getPlaylist().getFirstSong().getAlbumImageURL();
            getBitmap(imageURL);

            Picasso.with(context).load(imageURL).into(this.ivAlbumArt);
            tvSongName.setText(user.getPlaylist().getFirstSong().getName());

            //TODO: scrolling text doesn't work
            tvSongName.setSelected(true); //quick fix to get text view to scroll for long names
        } else {
            //sets default image and replaces album with an empty string
            this.ivAlbumArt.setImageResource(R.drawable.dj_live_icon_white_text);
            tvSongName.setText("");

            setBarColors(R.drawable.dj_live_background_header,
                    ContextCompat.getColor(context,R.color.colorPrimary),
                    ContextCompat.getColor(context,R.color.colorDarkPrimary));
        }
    }

    /**
     * This method sets the colors for the collapsible tool bar
     * @param backgroundImage
     * @param primaryColor
     * @param darkColor
     */
    public void setBarColors(int backgroundImage, int primaryColor, int darkColor) {
        //local variables
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout)
                findViewById(R.id.ctl_playlist_session);

        ColorDrawable colorDrawable = getColorDrawable();
        colorDrawable.setColor(primaryColor);

        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setBackgroundDrawable(colorDrawable);
        }

        //set colors/backgrounds
        ctl.setBackground(getDrawable(backgroundImage)); //change background image

        ctl.setContentScrimColor(primaryColor); //changes color of tabLayout

        //change color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkColor);
        }
    }

    private void getBitmap(String imageURL) {
        BitmapGeneratorTask runner = new BitmapGeneratorTask(this);
        runner.execute(imageURL);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * The navigation drawer allows the user to sign out of application and also shows the
     * host settings for the playlist
     */
    private void setUpNavigationDrawer() {
        //Adds drawer layout
        this.dlPlaylistSession = (DrawerLayout) findViewById(R.id.dl_playlistSession);

        //sets up navigation view
        this.nvPlaylistSession = (NavigationView) findViewById(R.id.nv_playlistSession);

        if (nvPlaylistSession != null) {

            nvPlaylistSession.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.nav_logout:
                                logout();
                                break;
                            default:

                        }

                        menuItem.setChecked(true);
                        //dlPlaylistSession.closeDrawers();
                        return true;
                    }
                });
        }
    }

    /**
     * Kills the player object to reduce the chance of memory leaks
     * This needs more work when leave playlist and terminate playlist are handled
     */
    @Override
    public void onDestroy() {

        if (user.isUserHost()) {
            player.pause(null);
            Log.d(TAG, "Player is supposed to be destroyed");
            super.destroyPlayer();
        }

        //TODO: logout()
        super.onDestroy();
    }

    /**
     * This onClick event pauses or plays songs in the spotify queue and updates appropriate
     * buttons
     * @param view
     */
    public void playOrPauseButtonClicked(View view) {
        //pause if it's playing, resume if it's not
        if (player.getPlaybackState().isPlaying) {
            player.pause(null);
        } else {
            player.resume(null);
        }
    }

    /**
     * Skips to next song in spotify queue and removes song from firebase
     * @param view
     */
    public void skipSongButtonClicked(View view) {
        player.skipToNext(null);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //if they're a host, remove the host username node in Firebase
        if(user.isUserHost()) {
            builder.setCancelable(true)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((HostPlaylistController) playlistController).logout();
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            builder.setCancelable(true)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //just remove their name from the list of users
                            ((GuestPlaylistController) playlistController).logout(user.getUsername());
                            dialogInterface.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    private void setUpGuestLogoutListener() {
        final Toast toast = Toast.makeText(this, "Logging out", Toast.LENGTH_LONG);
        if(user.isUserGuest()) {
            playlistController.refHost.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for(DataSnapshot node : dataSnapshot.getChildren()) {
                        //if the settings node
                        if(node.getKey().equals("settings")) {
                            toast.show();
                            ((GuestPlaylistController) playlistController).logout(user.getUsername());
                            finish();
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
    * Return color drawable object
    * @return ColorDrawable to change the color of the app bar
    */
    public ColorDrawable getColorDrawable() {
        return this.colorDrawable;
    }
}


