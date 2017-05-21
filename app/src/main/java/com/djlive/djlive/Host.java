package com.djlive.djlive;

public class Host extends User {
    /**
     * Constructor for a Host object
     */
    public Host() {
        super();
        authToken = null;
    }

    /**
     * This token is the authentication token that is returned from the Spotify login
     */
    private String authToken;

    /**
     * Getter method to return the host's Spotify authentication token value
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Setter method to update the value of the Spotify authentication token
     * @param token The token value from a Spotify login
     */
    public void setAuthToken(String token) {
        authToken = token;
    }


}
