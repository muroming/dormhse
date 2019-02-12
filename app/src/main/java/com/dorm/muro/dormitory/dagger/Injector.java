package com.dorm.muro.dormitory.dagger;

import android.content.Context;

import com.dorm.muro.dormitory.dagger.component.AppComponent;
import com.dorm.muro.dormitory.dagger.component.DaggerAppComponent;
import com.dorm.muro.dormitory.dagger.component.FirebaseComponent;
import com.dorm.muro.dormitory.dagger.component.ManagersComponent;
import com.dorm.muro.dormitory.dagger.component.PresentersComponent;
import com.dorm.muro.dormitory.dagger.module.AppModule;

import java.lang.ref.WeakReference;

import static com.dorm.muro.dormitory.Constants.SHARED_PREFERENCES;

public class Injector {
    private static Injector mInstance;
    private AppComponent mAppComponent;
    private ManagersComponent mManagersComponent;
    private PresentersComponent mPresenterComponent;

    private Injector() {
    }

    public static void init(Context context) {
        mInstance = new Injector();
        mInstance.mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(context, SHARED_PREFERENCES))
                .build();

        mInstance.mManagersComponent = mInstance.mAppComponent.plusFirebaseComponent().plusManagersComponent();
        mInstance.mPresenterComponent = mInstance.mManagersComponent.plusPresentersComponent();
    }

    public static Injector getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Injector's init method wasn't called");
        }

        return mInstance;
    }

    public ManagersComponent getManagersComponent() {
        return mManagersComponent;
    }

    public PresentersComponent getPresenterComponent() {
        return mPresenterComponent;
    }
}
