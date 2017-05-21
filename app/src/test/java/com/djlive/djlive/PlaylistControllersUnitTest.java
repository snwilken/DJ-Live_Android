package com.djlive.djlive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlaylistControllersUnitTest {
    @Test
    public void setUpHostPlaylistControllers() {
        String hostUsername = "Test Host Username";
        Long duration = 123456L;
        PlaylistController playlistController = new HostPlaylistController(hostUsername);
        HostPlaylistController hostPlaylistController = (HostPlaylistController) playlistController;

        assertNotNull(playlistController);
        assertNotNull(hostPlaylistController);

        String newHostUsername = "New Test Host Username";
        hostPlaylistController.setHostUsername(newHostUsername);

        HostPlaylist hostPlaylist = new HostPlaylist();
        hostPlaylist.setSettings(new Settings(12345, "Playlist", 4, true, 29349, 15, 98234098234L));
        hostPlaylistController.initInFirebase(hostPlaylist, newHostUsername);

        hostPlaylistController.addSong(new Song("uri", "Napalm Junction", "Run Through The Jungle",
                "Miles Davis", "Cool", true, duration, "albumImageURL"));
        hostPlaylistController.removeSong("uri");
        hostPlaylistController.removePlaylist();

    }

    @Test
    public void setUpGuestPlaylistController() {
        String hostUsername = "Test Host Username";
        PlaylistController playlistController = new GuestPlaylistController(hostUsername);
    }
}
