package com.dorm.muro.dormitory.dagger.module;

import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ManagersModule {
    @Singleton
    @Provides
    UserSessionManager providesUserSessionManager(FirebaseAuth mAuth, DatabaseReference mDatabase) {
        return new UserSessionManager(mAuth, mDatabase);
    }

    @Singleton
    @Provides
    ScheduleManager providesScheduleManager(DatabaseReference mDatabase) {
        return new ScheduleManager(mDatabase);
    }

    @Singleton
    @Provides
    TodoManager providesTodoManager(DatabaseReference mDatabase) {
        return new TodoManager(mDatabase);
    }
}
