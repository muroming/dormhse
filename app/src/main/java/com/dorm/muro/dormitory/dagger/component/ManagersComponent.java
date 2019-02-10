package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.FirebaseModule;
import com.dorm.muro.dormitory.dagger.module.ManagersModule;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ManagersModule.class, FirebaseModule.class})
public interface ManagersComponent {
    UserSessionManager providesUserSessionManager();
    ScheduleManager providesScheduleManager();
    TodoManager providesTodoManager();
}
