package com.dorm.muro.dormitory.network.ScheduleManagement;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.dorm.muro.dormitory.Constants.*;
public class ScheduleManagement {

    private static ScheduleManagement instance;
    private DatabaseReference mDatabase;

    private ScheduleManagement() {
    }

    public static ScheduleManagement getInstance() {
        if (instance == null) {
            instance = new ScheduleManagement();
            instance.mDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return instance;
    }

    public String createRoom(String userKey){
        String roomKey = mDatabase.child(ROOM_USERS_DATABASE).push().getKey();
        String pushKey = mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).push().getKey();
        mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).child(pushKey).setValue(userKey);

        return roomKey;
    }
}
