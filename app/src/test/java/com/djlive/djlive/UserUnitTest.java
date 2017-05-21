package com.djlive.djlive;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserUnitTest {
    @Test
    public void createEmptyHostAuthToken() {
        User user = new Host();
        Host host = (Host) user;
        assertNotNull(user);
        assertNotNull(host);

        assertNull(host.getAuthToken());

        String sampleAuthString = "sample auth string";
        host.setAuthToken(sampleAuthString);
        assertEquals(sampleAuthString, host.getAuthToken());

        user = new Guest();
        Guest guest = (Guest) user;
        assertNotNull(user);
        assertNotNull(guest);
    }

    @Test
    public void userGetSet() {
        User user = new Host();
        Host host = (Host) user;

        String hostUsername = "Test Host Username";
        host.setUsername(hostUsername);
        assertEquals(hostUsername, host.getUsername());

        Playlist playlist = new HostPlaylist();
        host.setPlaylist(playlist);
        assertEquals(playlist, host.getPlaylist());

        assertTrue(host.isUserHost());
        assertFalse(host.isUserGuest());
        assertNotNull(host);
        if(hostUsername.contains("goat")) {
            assertTrue(host.isUserGoat());
        } else {
            assertFalse(host.isUserGoat());
        }
    }
}
