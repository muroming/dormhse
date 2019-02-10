package com.dorm.muro.dormitory.dagger.module;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context mContext;
    private String sharedPreferencesName;

    public AppModule(Context mContext, String sharedPreferencesName) {
        this.mContext = mContext;
        this.sharedPreferencesName = sharedPreferencesName;
    }

    @Singleton
    @Provides
    Context providesAppContext(){
        return mContext;
    }

    @Singleton
    @Provides
    SharedPreferences providesSharedPreferences(Context context) {
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
    }
}
