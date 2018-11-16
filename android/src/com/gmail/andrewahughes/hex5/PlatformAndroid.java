package com.gmail.andrewahughes.hex5;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.badlogic.gdx.backends.android.AndroidApplication;

/**
 * Created by Andrew Hughes on 08/11/2018.
 */

public class PlatformAndroid extends AndroidApplication implements Platform {
    private Activity activity;
    @Override
    public void SetOrientation(String string) {
        if (string == "landscape"){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (string == "portrait"){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    public void setActivity(Activity activity){ this.activity = activity;   }
}