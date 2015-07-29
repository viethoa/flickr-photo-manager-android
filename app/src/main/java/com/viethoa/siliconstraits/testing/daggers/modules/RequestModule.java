package com.viethoa.siliconstraits.testing.daggers.modules;

import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.daggers.managers.impl.FlickrManagerImpl;

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
            FlickrManager.class
        }
)

public class RequestModule {

    @Provides
    @Singleton
    public FlickrManager providesFlickrManager() {
        return new FlickrManagerImpl();
    }

}
