package com.djlive.djlive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SettingsUnitTest {
    private Settings settings;
    @Test
    public void verifySetGet() throws Exception {
        int pin = 123456;
        String name = "Playlist name is really cool";
        int songsPerUser = 15;
        int maxLength = 234890;
        int maxGuests = 14;
        long timeStamp = 987234987234l;
        boolean allowExplicit = true;

        settings = new Settings(pin, name, songsPerUser, allowExplicit, maxLength, maxGuests, timeStamp);

        settings.setAllowExplicit(allowExplicit);
        assertEquals(allowExplicit, settings.getAllowExplicit());

        settings.setMaxGuests(maxGuests);
        assertEquals(maxGuests, settings.getMaxGuests());

        settings.setMaxLength(maxLength);
        assertEquals(maxLength, settings.getMaxLength());

        settings.setName(name);
        assertEquals(name, settings.getName());

        settings.setPin(pin);
        assertEquals(pin, settings.getPin());

        settings.setSongsPerUser(songsPerUser);
        assertEquals(songsPerUser, settings.getSongsPerUser());

        settings.setTimeStamp(timeStamp);
        assertEquals(timeStamp, settings.getTimeStamp());
    }

    @Test
    public void defaultConstructorNonNull() {
        Settings settings = new Settings();
        assertNotNull(settings);
    }
}
