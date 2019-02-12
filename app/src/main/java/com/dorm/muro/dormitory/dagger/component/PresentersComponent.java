package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.PresentersModule;
import com.dorm.muro.dormitory.dagger.scopes.PresentersScope;
import com.dorm.muro.dormitory.presentation.login.LoginActivity;
import com.dorm.muro.dormitory.presentation.options.OptionsFragment;
import com.dorm.muro.dormitory.presentation.payment.PaymentFragment;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;
import com.dorm.muro.dormitory.presentation.todo.TodoFragment;

import dagger.Subcomponent;

@PresentersScope
@Subcomponent(modules = PresentersModule.class)
public interface PresentersComponent {
    void inject(ScheduleFragment fragment);
    void inject(LoginActivity activity);
    void inject(OptionsFragment fragment);
    void inject(PaymentFragment fragment);
    void inject(TodoFragment fragment);
}
