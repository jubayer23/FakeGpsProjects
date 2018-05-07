package com.app.fakegps.appdata;

import android.app.Application;

import com.app.fakegps.sharedprefs.PrefManager;


public class MydApplication extends Application {


    public static final String TAG = MydApplication.class.getSimpleName();

    private static MydApplication mInstance;

    private static PrefManager pref;

    private float scale;

    public static String deviceImieNumber = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.scale = getResources().getDisplayMetrics().density;

        pref = new PrefManager(this);

    }

    public static synchronized MydApplication getInstance() {
        return mInstance;
    }


    public PrefManager getPrefManger() {
        if (pref == null) {
            pref = new PrefManager(this);
        }

        return pref;
    }


    public int getPixelValue(int dps) {
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
}
