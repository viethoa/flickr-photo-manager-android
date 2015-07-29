package com.viethoa.siliconstraits.testing;

import com.viethoa.siliconstraits.testing.daggers.modules.AppModule;
import com.viethoa.siliconstraits.testing.utils.AppUtils;

import dagger.ObjectGraph;

/**
 * Created by VietHoa on 06/07/15.
 */
public class MyApplication extends AppUtils {

    protected ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        buildObjectGraph();
    }

    protected void buildObjectGraph() {
        objectGraph = ObjectGraph.create(new AppModule(this));
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public ObjectGraph extendScope(Object... modules) {
        if (modules != null) {
            return objectGraph.plus(modules);
        } else {
            return objectGraph;
        }
    }
}
