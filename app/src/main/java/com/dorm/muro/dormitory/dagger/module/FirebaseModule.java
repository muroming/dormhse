package com.dorm.muro.dormitory.dagger.module;

import com.dorm.muro.dormitory.dagger.scopes.FirebaseScope;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @FirebaseScope
    @Provides
    public FirebaseAuth providesFirebaseAuthentication() {
        return FirebaseAuth.getInstance();
    }

    @FirebaseScope
    @Provides
    public DatabaseReference providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
