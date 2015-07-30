package com.viethoa.siliconstraits.testing.daggers.modules;

import android.content.Context;

import com.viethoa.siliconstraits.testing.controllers.LoginActivity;
import com.viethoa.siliconstraits.testing.controllers.MainActivity;
import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.daggers.managers.impl.FlickrManagerImpl;
import com.viethoa.siliconstraits.testing.services.UserPhotoService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by VietHoa on 03/05/15.
 */

@Module(
        complete = false,
        library = true,
        injects = {
                FlickrManager.class,
                LoginActivity.class,
                MainActivity.class,
                UserPhotoService.class
        }
)

public class RequestModule {

    @Provides
    @Singleton
    public FlickrManager providesFlickrManager(Context applicationContext) {
        return new FlickrManagerImpl(applicationContext);
    }

}
