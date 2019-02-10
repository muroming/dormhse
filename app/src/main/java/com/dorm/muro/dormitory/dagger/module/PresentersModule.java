package com.dorm.muro.dormitory.dagger.module;

import android.content.SharedPreferences;

import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    ScheduleFragmentPresenter provideScheduleFragmentPresenter(SharedPreferences preferences,
                                                                      UserSessionManager userSessionManager, ScheduleManager scheduleManager) {
        return new ScheduleFragmentPresenter(preferences, userSessionManager, scheduleManager);
    }
}
