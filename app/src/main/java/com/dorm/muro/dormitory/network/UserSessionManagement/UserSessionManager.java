package com.dorm.muro.dormitory.network.UserSessionManagement;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

import static com.dorm.muro.dormitory.Constants.*;


public class UserSessionManager {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Inject
    public UserSessionManager(FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    public Task<AuthResult> authenticate(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> registerNewUser(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> updateUserPassword(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updatePassword(newPassword);
    }

    public Task<Void> updateUserEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updateEmail(newEmail);
    }

    public PublishSubject<String[]> getUserInfo() {
        PublishSubject<String[]> subject = PublishSubject.create();
        String userKey = mAuth.getCurrentUser().getUid();

        mDatabase.child(USER_INFO_DATABASE).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> info = (Map<String, String>) dataSnapshot.getValue();
                String fio = info.get(USER_SURNAME_FIELD) + " " + info.get(USER_NAME_FIELD) + " " + info.get(USER_PATRONYMIC_FIELD);

                subject.onNext(new String[]{fio, info.get(USER_CONTRACT_ID_FIELD), mAuth.getCurrentUser().getEmail()});
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return subject;
    }

    public Task<Void> updateUserField(Map<String, Object> newValues) {
        if (mAuth.getCurrentUser() == null) {
            return null;
        }
        String id = mAuth.getCurrentUser().getUid();
        return mDatabase.child(USER_INFO_DATABASE).child(id).updateChildren(newValues);
    }

    public Task<Void> sendRestoreEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void logout() {
        mAuth.signOut();
    }
}
