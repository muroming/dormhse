package com.dorm.muro.dormitory.dagger.module;

import com.dorm.muro.dormitory.dagger.scopes.ManagersScope;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import dagger.Module;
import dagger.Provides;

@Module
public class ManagersModule {
    @ManagersScope
    @Provides
    UserSessionManager providesUserSessionManager(FirebaseAuth mAuth, DatabaseReference mDatabase) {
        return new UserSessionManager(mAuth, mDatabase);
    }

    @ManagersScope
    @Provides
    ScheduleManager providesScheduleManager(DatabaseReference mDatabase) {
        return new ScheduleManager(mDatabase);
    }

    @ManagersScope
    @Provides
    TodoManager providesTodoManager(DatabaseReference mDatabase) {
        return new TodoManager(mDatabase);
    }
}
