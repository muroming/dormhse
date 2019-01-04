package com.dorm.muro.dormitory.network.authentication;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import durdinapps.rxfirebase2.RxFirebaseAuth;
import io.reactivex.Maybe;

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

    public Maybe<AuthResult> authenticate(String email, String password) {
        return RxFirebaseAuth.signInWithEmailAndPassword(mAuth, email, password);
    }

    public Maybe<AuthResult> registerNewUser(String email, String password) {
       return RxFirebaseAuth.createUserWithEmailAndPassword(mAuth, email, password);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void logout() {
        mAuth.signOut();
    }
}
