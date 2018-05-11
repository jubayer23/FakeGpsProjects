package com.app.fakegps.appdata;

import android.app.Application;

import com.app.fakegps.sharedprefs.PrefManager;
import com.google.android.gms.ads.MobileAds;


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

        MobileAds.initialize(this, "ca-app-pub-5364782604988219~5686491126");

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
