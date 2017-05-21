package com.djlive.djlive;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * This tests the song pojo
 * Created by JohnB on 11/14/2016.
 */

public class SongUnitTest {
    @Test
    public void emptySongSetupWork() throws Exception {
        Song song = new Song();
        Long duration = 122222223400505004L;

        song.setAddedBy("Ya Boy");
        assertEquals(song.getAddedBy(), "Ya Boy");

        song.setAlbum("Da best album 2016");
        assertEquals(song.getAlbum(), "Da best album 2016");

        song.setArtist("#2016");
        assertEquals(song.getArtist(), "#2016");

        song.setName("Words are fun");
        assertEquals(song.getName(), "Words are fun");

        song.setURI("1234567");
        assertEquals(song.getURI(), "1234567");

        song.setIsExplicit(true);
        assertEquals(song.getIsExplicit(), true);

        song.setDuration_ms(duration);
        assertEquals(song.getDuration_ms(), duration);

        song.setAlbumImageURL("https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d");
        assertEquals(song.getAlbumImageURL(),
                "https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d");
    }

    @Test
    public void songSetupWork() throws Exception{
        Long duration = 5432939495L;
        String albumURL = "https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d";
        Song song = new Song("1234567", "#2016", "Da best album 2016", "Ya Boy",
                "Words are fun", true, duration, albumURL);
        assertEquals(song.getAddedBy(), "Ya Boy");
        assertEquals(song.getAlbum(), "Da best album 2016");
        assertEquals(song.getArtist(), "#2016");
        assertEquals(song.getName(), "Words are fun");
        assertEquals(song.getURI(), "1234567");
        assertEquals(song.getIsExplicit(), true);
        assertEquals(song.getDuration_ms(), duration);
        assertEquals(song.getAlbumImageURL(), albumURL);
    }
}
