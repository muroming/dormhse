package com.dorm.muro.dormitory.network.ScheduleManagement;


import android.support.annotation.NonNull;
import android.util.Pair;

import com.dorm.muro.dormitory.presentation.schedule.ScheduleCell;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
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

    public String uploadDuty(String flatKey, int roomNumber, ScheduleCell startDate, ScheduleCell endDate) {
        Map<String, Object> duty = new HashMap<>();
        duty.put(DUTY_ROOM, roomNumber);
        duty.put(DUTY_START, startDate);
        duty.put(DUTY_END, endDate);

        String pushKey = mDatabase.child(DUTIES_DATABASE).push().getKey();
        mDatabase.child(DUTIES_DATABASE).child(pushKey).setValue(duty);

        String flatPushKey = mDatabase.child(ROOM_DUTIES_DATABASE).child(flatKey).push().getKey();
        mDatabase.child(ROOM_DUTIES_DATABASE).child(flatKey).child(flatPushKey).setValue(pushKey);

        return pushKey;
    }

    public void updateDuty(String dutyKey, int roomNumber, Date startDate, Date endDate) {
        Map<String, Object> duty = new HashMap<>();
        duty.put(DUTY_ROOM, roomNumber);
        duty.put(DUTY_START, startDate);
        duty.put(DUTY_END, endDate);

        mDatabase.child(DUTIES_DATABASE).child(dutyKey).updateChildren(duty);
    }

    public DatabaseReference getFlatDuties(String flatKey) {
        return  mDatabase.child(ROOM_DUTIES_DATABASE).child(flatKey);
    }

    public PublishSubject<Map<String, Object>> getDutyByKey(String dutyKey) {
        PublishSubject subject = PublishSubject.create();

        mDatabase.child(DUTIES_DATABASE).child(dutyKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> duty = new HashMap<>();
                duty.put(DUTY_ROOM, dataSnapshot.child(DUTY_ROOM).getValue(Long.class));
                duty.put(DUTY_START, dataSnapshot.child(DUTY_START).getValue(ScheduleCell.class));
                duty.put(DUTY_END, dataSnapshot.child(DUTY_END).getValue(ScheduleCell.class));

                subject.onNext(duty);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return subject;
    }
}
