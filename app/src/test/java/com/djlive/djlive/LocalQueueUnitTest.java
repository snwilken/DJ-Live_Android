package com.djlive.djlive;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This tests the local queue model object
 * Created by JohnB on 11/14/2016.
 */

public class LocalQueueUnitTest {
    // Two lines not tested for sum reason in LocalQueue
    @Test
    public void localQueueTest() throws Exception{
        Long duration = 122222223400505004L;
        String albumURL = "https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d";

        Song song = new Song("1234567", "#2016", "Da best album 2016", "Ya Boy",
                "Words are fun", true, duration, albumURL);

        ArrayList<Song> tmp = new ArrayList<>();
        tmp.add(song);

        LocalQueue q = new LocalQueue();

        //assertEquals(q.getSongAt(), null);

        //assertEquals(q.addSong(song), true);
        //assertEquals(q.addSong(song), false);
        //assertEquals(q.getSongAt(), song);

        assertEquals(q.getLocalQueue(),tmp);

        assertEquals(q.removeSong("1234567"), song);
        assertEquals(q.removeSong("why hello there"), null);

    }
}
