package com.bluechip.inventory.utilities;

import android.app.Application;
import android.content.Context;

/**
 * Created by aspl on 14/11/17.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setFont(Context mainActivity){
        FontsOverride.setDefaultFont(mainActivity, "DEFAULT", "CenturyGothic.ttf");
        FontsOverride.setDefaultFont(mainActivity, "MONOSPACE", "CenturyGothic.ttf");
        FontsOverride.setDefaultFont(mainActivity, "SERIF", "CenturyGothic.ttf");
        FontsOverride.setDefaultFont(mainActivity, "SANS_SERIF", "CenturyGothic.ttf");
    }
}
