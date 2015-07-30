package com.viethoa.siliconstraits.testing.daggers.modules;

import android.app.Application;
import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viethoa.siliconstraits.testing.MyApplication;
import com.viethoa.siliconstraits.testing.utils.CustomDateParser;

import java.lang.ref.WeakReference;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Created by VietHoa on 03/05/15.
 */
@Module(
        library = true,
        complete = false,
        injects = {
                MyApplication.class
        }
)

public class AppModule {

    private WeakReference<Application> appWeakRef;

    public AppModule(Application context) {
        this.appWeakRef = new WeakReference<Application>(context);
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        if (appWeakRef != null) {
            return appWeakRef.get();
        } else {
            return null;
        }
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public Gson providesJsonConverter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new CustomDateParser())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        return gson;
    }
}

