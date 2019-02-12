package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.FirebaseModule;
import com.dorm.muro.dormitory.dagger.scopes.FirebaseScope;
import com.dorm.muro.dormitory.presentation.resetPassword.ResetPasswordActivity;

import dagger.Subcomponent;

@Subcomponent(modules = FirebaseModule.class)
@FirebaseScope
public interface FirebaseComponent {
    ManagersComponent plusManagersComponent();
}
