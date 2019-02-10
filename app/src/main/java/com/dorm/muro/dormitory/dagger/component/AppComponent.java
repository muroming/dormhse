package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {
    AppModule.class
})
public interface AppComponent {
    PresentersComponent getPresentersComponent();
}
