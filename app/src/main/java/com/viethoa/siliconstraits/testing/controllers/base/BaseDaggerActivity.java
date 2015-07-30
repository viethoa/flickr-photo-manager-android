package com.viethoa.siliconstraits.testing.controllers.base;

import android.os.Bundle;

import com.viethoa.siliconstraits.testing.MyApplication;
import com.viethoa.siliconstraits.testing.daggers.modules.RequestModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by VietHoa on 05/05/15.
 */
public abstract class BaseDaggerActivity extends BaseActivity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication application = (MyApplication) getApplication();
        Object[] modules = null;
        if (getModules() != null) {
            modules = getModules().toArray();
        }
        activityGraph = application.extendScope(modules);
        activityGraph.inject(this);

        super.onCreate(savedInstanceState);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new RequestModule());
    }

}