package com.dorm.muro.dormitory.dagger.module;

import android.content.SharedPreferences;

import com.dorm.muro.dormitory.dagger.scopes.PresentersScope;
import com.dorm.muro.dormitory.network.ScheduleManagement.ScheduleManager;
import com.dorm.muro.dormitory.network.TodoManagement.TodoManager;
import com.dorm.muro.dormitory.network.UserSessionManagement.UserSessionManager;
import com.dorm.muro.dormitory.presentation.login.LoginPresenter;
import com.dorm.muro.dormitory.presentation.options.OptionsPresenter;
import com.dorm.muro.dormitory.presentation.payment.PaymentPresenter;
import com.dorm.muro.dormitory.presentation.schedule.SchedulePresenter;
import com.dorm.muro.dormitory.presentation.todo.TodoPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    @PresentersScope
    SchedulePresenter provideSchedulePresenter(SharedPreferences preferences,
                                                       UserSessionManager userSessionManager, ScheduleManager scheduleManager) {
        return new SchedulePresenter(preferences, userSessionManager, scheduleManager);
    }

    @Provides
    @PresentersScope
    TodoPresenter provideTodoPresenter(TodoManager todoManager) {
        return new TodoPresenter(todoManager);
    }

    @Provides
    @PresentersScope
    LoginPresenter provideLoginPresenter(SharedPreferences preferences,
                                         UserSessionManager userSessionManager) {
        return new LoginPresenter(userSessionManager, preferences);
    }

    @Provides
    @PresentersScope
    OptionsPresenter provideOptionPresenter(SharedPreferences preferences,
                                            UserSessionManager userSessionManager, ScheduleManager scheduleManager) {
        return new OptionsPresenter(preferences, userSessionManager, scheduleManager);
    }

    @Provides
    @PresentersScope
    PaymentPresenter providePaymentPresenter(SharedPreferences preferences) {
        return new PaymentPresenter(preferences);
    }
}
