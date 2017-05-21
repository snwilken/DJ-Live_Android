package com.djlive.djlive;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlaylistUnitTests {
    @Test
    public void playlistIsSetUp() throws Exception {

        HostPlaylist hostPlaylist = new HostPlaylist();
        GuestPlaylist guestPlaylist = new GuestPlaylist();

        assertNotNull(hostPlaylist);
        assertNotNull(guestPlaylist);

        Settings settings = new Settings();
        hostPlaylist.setSettings(settings);

        assertSame(hostPlaylist.getSettings(), settings);

        hostPlaylist.setPlaylistName("hiImAPlaylist");

        assertEquals("hiImAPlaylist", hostPlaylist.getPlaylistName());

        Song song = new Song();

        hostPlaylist.addSong(song);

        assertNull(hostPlaylist.getPlaylistQueue());
    }
}
