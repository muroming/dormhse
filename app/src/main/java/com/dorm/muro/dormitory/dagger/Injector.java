package com.dorm.muro.dormitory.dagger;

import android.content.Context;

import com.dorm.muro.dormitory.dagger.component.AppComponent;
import com.dorm.muro.dormitory.dagger.component.PresentersComponent;
import com.dorm.muro.dormitory.dagger.module.AppModule;

import java.lang.ref.WeakReference;

import static com.dorm.muro.dormitory.Constants.SHARED_PREFERENCES;

public class Injector {
    private static Injector mInstance;
    private AppComponent mAppComponent;

    private WeakReference<PresentersComponent> mPresentersComponent;

    private Injector(){
    }

    public static void init(Context context) {
        getInstance().mAppComponent = DaggerAppComponent().builder()
                .appModule(new AppModule(context, SHARED_PREFERENCES))
                .build();
    }

    public static Injector getInstance() {
        if (mInstance == null) {
            mInstance = new Injector();
        }

        return mInstance;
    }

    public PresentersComponent getScheduleManager() {
        if (mPresentersComponent.get() == null) {
            mPresentersComponent = new WeakReference<>(mAppComponent.getPresentersComponent());
        }
        return mPresentersComponent.get();
    }
}
