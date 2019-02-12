package com.dorm.muro.dormitory.dagger.module;

import com.dorm.muro.dormitory.dagger.scopes.ManagersScope;
import com.dorm.muro.dormitory.network.ScheduleManagement.IScheduleManager;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.TodoManagement.ITodoManager;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.IUserSessionManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import dagger.Module;
import dagger.Provides;

@Module
public class ManagersModule {
    @ManagersScope
    @Provides
    IUserSessionManager providesUserSessionManager(FirebaseAuth mAuth, DatabaseReference mDatabase) {
        return new UserSessionManager(mAuth, mDatabase);
    }

    @ManagersScope
    @Provides
    IScheduleManager providesScheduleManager(DatabaseReference mDatabase) {
        return new ScheduleManager(mDatabase);
    }

    @ManagersScope
    @Provides
    ITodoManager providesTodoManager(DatabaseReference mDatabase) {
        return new TodoManager(mDatabase);
    }
}
