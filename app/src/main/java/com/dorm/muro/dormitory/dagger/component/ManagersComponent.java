package com.dorm.muro.dormitory.dagger.component;

import com.dorm.muro.dormitory.dagger.module.ManagersModule;
import com.dorm.muro.dormitory.dagger.scopes.ManagersScope;
import com.dorm.muro.dormitory.presentation.createTodo.CreateTodoActivity;
import com.dorm.muro.dormitory.presentation.main.MainActivity;
import com.dorm.muro.dormitory.presentation.resetPassword.ResetPasswordActivity;


import dagger.Subcomponent;

@ManagersScope
@Subcomponent(modules = ManagersModule.class)
public interface ManagersComponent {
    PresentersComponent plusPresentersComponent();
    void inject(ResetPasswordActivity activity);
    void inject(MainActivity activity);
    void inject(CreateTodoActivity activity);
}
