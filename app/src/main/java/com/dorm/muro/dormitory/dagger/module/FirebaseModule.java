package com.dorm.muro.dormitory.dagger.module;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @Singleton
    @Provides
    public FirebaseAuth providesFirebaseAuthentication() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    public DatabaseReference providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
