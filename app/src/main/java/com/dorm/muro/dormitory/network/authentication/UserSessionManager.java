package com.dorm.muro.dormitory.network.authentication;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import static com.dorm.muro.dormitory.Constants.*;


public class UserSessionManager {

    private static UserSessionManager instance;

    private UserSessionManager() {
    }

    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    //todo inject
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
