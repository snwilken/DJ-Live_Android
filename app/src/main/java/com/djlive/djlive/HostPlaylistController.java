package com.djlive.djlive;

public class HostPlaylistController extends PlaylistController {

    public HostPlaylistController(String hostUsername) {
        super(hostUsername);
    }

    public void removeSong(String songURI) {

    }

    public void removePlaylist() {
        //delete the node under hostUsername
    }

    public void logout() {
        refHost.removeValue();
    }
}
