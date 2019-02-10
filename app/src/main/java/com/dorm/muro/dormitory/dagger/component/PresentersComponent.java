package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.ManagersModule;
import com.dorm.muro.dormitory.dagger.module.PresentersModule;
import com.dorm.muro.dormitory.dagger.scopes.PresentersScope;
import com.dorm.muro.dormitory.presentation.schedule.ScheduleFragment;

import dagger.Subcomponent;

@PresentersScope
@Subcomponent(modules = {
        PresentersModule.class,
        ManagersModule.class
})
public interface PresentersComponent {
    void inject(ScheduleFragment fragment);
}
