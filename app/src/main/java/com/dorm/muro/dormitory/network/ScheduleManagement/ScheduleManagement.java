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

    public void createRoom(String userKey, String roomNum, String roomId){
        String roomKey = mDatabase.child(ROOM_USERS_DATABASE).push().getKey();
        String pushKey = mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).push().getKey();
        mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).child(pushKey).setValue(userKey);
        mDatabase.child(ROOMS_DATABASE).child(roomKey).setValue(roomNum);
        mDatabase.child(ID_ROOM_DATABASE).child(roomId).setValue(roomKey);
    }

    public PublishSubject<Boolean> joinRoom(String userKey, String roomId) {
        PublishSubject<Boolean> subject = PublishSubject.create();

        mDatabase.child(ID_ROOM_DATABASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(roomId)) {
                    String roomKey = (String) ((Map) dataSnapshot.getValue()).get(roomId);
                    String userPushKey = mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).push().getKey();
                    Map<String, Object> upd = new HashMap<>(1);

                    upd.put(userPushKey, userKey);
                    mDatabase.child(ROOM_USERS_DATABASE).child(roomKey).updateChildren(upd);

                    subject.onNext(true);
                } else {
                    subject.onNext(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subject.onNext(false);
            }
        });

        return subject;
    }
}
