package com.djlive.djlive;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class GuestPlaylistController extends PlaylistController {

    public GuestPlaylistController(String hostUsername) {
        super(hostUsername);
    }

    public void logout(final String username) {
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()) {
                    if(user.getValue().equals(username)) {
                        refUsers.child(user.getKey()).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
