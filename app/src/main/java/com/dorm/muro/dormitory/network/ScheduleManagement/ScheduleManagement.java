package com.dorm.muro.dormitory.network.ScheduleManagement;


import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

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

    public String createRoom(String userKey, String roomNum, String roomId) {
        String roomKey = mDatabase.child(ROOM_USERS_DATABASE).push().getKey();
        String pushKey = mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).push().getKey();
        mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).child(pushKey).setValue(userKey);
        mDatabase.child(ROOMS_DATABASE).child(roomKey).setValue(roomNum);
        mDatabase.child(ID_ROOM_DATABASE).child(roomId).setValue(roomKey);

        return roomKey;
    }

    public PublishSubject<String> joinRoom(String userKey, String roomId) {
        PublishSubject<String> subject = PublishSubject.create();

        mDatabase.child(ID_ROOM_DATABASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(roomId)) {
                    String roomKey = (String) ((Map) dataSnapshot.getValue()).get(roomId);
                    String userPushKey = mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).push().getKey();
                    Map<String, Object> upd = new HashMap<>(1);

                    upd.put(userPushKey, userKey);
                    mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).updateChildren(upd);

                    subject.onNext(roomKey);
                } else {
                    subject.onNext("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onNext("");
            }
        });

        return subject;
    }

    public void leaveRoom(String userKey, String roomId) {
        mDatabase.child(ROOM_USERS_DATABASE).child(roomId).orderByValue().equalTo(userKey).getRef().removeValue();
        mDatabase.child(ROOM_USERS_DATABASE).child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {  // no members left
                    mDatabase.child(ROOMS_DATABASE).child(roomId).removeValue();
                    mDatabase.child(ID_ROOM_DATABASE).orderByValue().equalTo(roomId).getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
