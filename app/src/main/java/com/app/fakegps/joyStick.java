package com.app.fakegps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import java.util.concurrent.Executor;

import static com.app.fakegps.AppConstants.PREF_NAME;

public class joyStick extends Service {
    public joyStick() {
    }

    Thread thread;
    boolean isIntrupted = false;
    double angle;
    SharedPreferences pref;
    boolean isMveing = false;
    static private LocationManager locationManager;
    private WindowManager windowManager;
    private View joystickobj;
    LinearLayout relativeLayout;
    Location location;
    static float deviceDensity;
    int screenWidth = 1080;
    WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        screenWidth = intent2.getIntExtra("SCREEN_SIZE", 1000);
        deviceDensity = intent2.getFloatExtra("SCREEN_DENSITY", 3);
        Log.d("ONSTART", screenWidth + "");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("ONSTART", 2 + "");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Intent notificationIntent = new Intent(joyStick.this, MapsActivity.class);


        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel("com.app.fakegps");
            if (mChannel == null) {
                mChannel = new NotificationChannel("com.app.fakegps", "com.app.fakegps", importance);
                mChannel.setDescription("com.app.fakegps");
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, "com.app.fakegps")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Fake Gps is currently running in background")
                .setChannelId("com.app.fakegps")

                .setPriority(Notification.PRIORITY_DEFAULT)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher));
        Notification notification = nBuilder.build();


        notification.contentIntent = intent;

        notification.flags = Notification.FLAG_NO_CLEAR;
        startForeground(42, notification);
        joystickobj = layoutInflater.inflate(R.layout.joystic, null);
        relativeLayout = joystickobj.findViewById(R.id.mainlayout);
        ImageView mover = (ImageView) joystickobj.findViewById(R.id.mover);
        Joystick joystick = (Joystick) joystickobj.findViewById(R.id.joystick);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {


            locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                    "requiresNetwork" == "",
                    "requiresSatellite" == "",
                    "requiresCell" == "",
                    "hasMonetaryCost" == "",
                    "supportsAltitude" == "",
                    "supportsSpeed" == "",
                    "supportsBearing" == "",
                    Criteria.POWER_HIGH,
                    android.location.Criteria.ACCURACY_FINE);
        } catch (Exception ee) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to Select Fake Gps as a Mock location app");
// Add the buttons
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


// Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
                isMveing = true;
            }

            @Override
            public void onDrag(float degrees, float offset) {
                if (degrees < 0) {
                    degrees = degrees + 360;
                }
                /*if(degrees >= 0 && degrees < 90){
                    degrees = 180 - degrees;
                }else if(degrees > 270 && degrees < 360){
                    degrees = 270 - (degrees - 270);
                }else if(degrees > 180 && degrees < 270){
                    degrees = 270 + (270 - degrees);
                }else if(degrees > 90 && degrees < 180){
                    degrees = 180 - degrees;
                }else if(degrees == 350){
                    degrees = 180;
                }else if(degrees == 180){
                    degrees = 360;
                }*/
                angle = degrees;

               // Log.d("DEBUG",String.valueOf(angle));
            }

            @Override
            public void onUp() {
                isMveing = false;
            }
        });


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 200;

        windowManager.addView(joystickobj, params);
        mover.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(joystickobj, params);
                        return true;
                }
                return false;
            }
        });
        thread = new PrimeThread();
        thread.start();
        return super.onStartCommand(intent2, flags, startId);
    }

    class PrimeThread extends Thread {


        @SuppressLint("MissingPermission")
        public void run() {
            while (!isIntrupted) {
                if (isMveing) {

                    if (MapsActivity.getInstance() != null) {
                        Log.d("JoyStick", "ACTIVITY_MOVE");
                        MapsActivity.getInstance().movePointer(angle);
                    } else {
                        if (location == null) {
                            Log.d("JoyStick", "NULL_LOCATION");
                            location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(Double.parseDouble(pref.getString(AppConstants.LATITUDE, "0.0")));
                            location.setLongitude(Double.parseDouble(pref.getString(AppConstants.LONGITUDE, "0.0")));
                            location.setAccuracy(5);
                            location.setTime(System.currentTimeMillis());

                        } else {
                            Log.d("JoyStick", "SERVICE_MOVE");
                            movePointer(angle);
                        }

                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    LatLng getNewLatlng(double distance, double angle, double latitude, double longitude) {

        double dx = distance * Math.cos(Math.toRadians(angle));
        double dy = distance * Math.sin(Math.toRadians(angle));
        Log.d("ANGEL", "" + angle + "/" + dx + "/" + dy);

        double r_earth = 6378000.0;
        Double new_latitude = latitude + (dy / r_earth) * (180 / Math.PI);
        Double new_longitude = longitude + ((dx / r_earth) * (180 / Math.PI) / Math.cos(latitude));
        return new LatLng(new_latitude, new_longitude);
    }

    public void movePointer(double angle) {
        if (location != null) {
            LatLng newlatlng = getNewLatlng(20.0, angle, location.getLatitude(), location.getLongitude());
            try {

                MockGpsProvider mMockGpsProviderTask = new MockGpsProvider();
                mMockGpsProviderTask.execute(newlatlng);

            } catch (Exception ee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to Select Fake Gps as a Mock location app");
// Add the buttons
                builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


// Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            //  mMap.animateCamera(CameraUpdateFactory.newLatLng(newlatlng));
        }

    }

    private class MockGpsProvider extends AsyncTask<LatLng, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        public static final String LOG_TAG = "GpsMockProvider";
        public static final String GPS_MOCK_PROVIDER = "GpsMockProvider";

        /**
         * Keeps track of the currently processed coordinate.
         */
        public Integer index = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected Void doInBackground(LatLng... data) {
            // process data
            try {


                // let UI Thread know which coordinate we are processing
                publishProgress(index);
                // empty or invalid line

                // translate to actual GPS location
                Log.d("SET_MOCK", data[0].latitude + "--" + data[0].longitude);
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(data[0].latitude);
                location.setLongitude(data[0].longitude);
                location.setAccuracy(5);
//                location.setBearing(3.14F);
//                location.setSpeed(2);
//                location.setAltitude(10);
                location.setTime(System.currentTimeMillis());
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

                // Thread.sleep(300);

                // sleep for a while before providing next location
            } catch (Exception ee) {
                Log.d("SS", ee.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(LOG_TAG, "onProgressUpdate():" + values[0]);

        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        isIntrupted = true;

        if (joystickobj != null) windowManager.removeView(joystickobj);
        super.onDestroy();
    }


}

