package com.app.fakegps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.testfairy.TestFairy;

import org.json.JSONObject;

import java.util.List;

import static com.app.fakegps.AppConstants.PREF_NAME;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    LocationRequest mLocationRequest;
    SharedPreferences pref;
    Location mCurrentLocation;
    private GoogleMap mMap;
    Marker marker = null;
    private static final int PERMISSIONS_REQUEST_ALLERT = 101;
    private static final int PERMISSIONS_REQUEST_LOCATION = 102;
    Toolbar toolbar;
    static float deviceDensity;
    int screenWidth = 0;
    PlaceAutocompleteFragment autocompleteFragment;
    View view;
    FloatingActionButton stopBtn;
    private static final String MOCK_GPS_PROVIDER_INDEX = "GpsMockProviderIndex";
    public static MapsActivity mapsActivity;
    private MockGpsProvider mMockGpsProviderTask = null;
    private Integer mMockGpsProviderIndex = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager locationManager;
    Location location;

    private GoogleApiClient mGoogleApiClient;
    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    public boolean isSearchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  TestFairy.begin(this, "8565aa935052dd00b031b3ab6a489943969a321d");
        deviceDensity = getResources().getDisplayMetrics().density;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        mapsActivity = this;
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (savedInstanceState instanceof Bundle) {
            /** Let's find out where we were. */
            mMockGpsProviderIndex = savedInstanceState.getInt(MOCK_GPS_PROVIDER_INDEX, 0);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        setContentView(R.layout.activity_maps);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        view = autocompleteFragment.getView();
        view.setVisibility(View.GONE);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                view.setVisibility(View.GONE);
                autocompleteFragment.setText("");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                view.setVisibility(View.GONE);
                autocompleteFragment.setText("");

            }
        });




        toolbar = (Toolbar) findViewById(R.id.toolbarDSettings);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        stopBtn = (FloatingActionButton) findViewById(R.id.stopBtn);
        setSupportActionBar(toolbar);
        if (isMyServiceRunning(joyStick.class, getApplicationContext())) {
            stopBtn.setVisibility(View.VISIBLE);
        }
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                if (isMyServiceRunning(joyStick.class, getApplicationContext())) {
                    stopService(new Intent(MapsActivity.this, joyStick.class));
                }
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
                stopBtn.setVisibility(View.GONE);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    android.location.Criteria.POWER_LOW,
                    android.location.Criteria.ACCURACY_FINE);
        } catch (Exception ee) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            testPermission();
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

                mMockGpsProviderTask = new MockGpsProvider();
                mMockGpsProviderTask.execute(newlatlng);
                if (!isMyServiceRunning(joyStick.class, getApplicationContext())) {
                    startService(new Intent(MapsActivity.this, joyStick.class).putExtra("SCREEN_SIZE", screenWidth).putExtra("SCREEN_DENSITY", deviceDensity));
                }
            } catch (Exception ee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
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
        } else {
            Log.d("JoyStick", "ACTIVITY_NULL_LOCATION");
        }

    }

    public static MapsActivity getInstance() {
        return mapsActivity;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
                testPermission();
            } else {

                Toast.makeText(this, "Location Permission is needed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSIONS_REQUEST_ALLERT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // You have permission
                }
            }
        }
    }

    public void testPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, PERMISSIONS_REQUEST_ALLERT);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location3) {
                // Got last known location. In some rare situations this can be null.
                if (location3 != null) {
                    // Logic to handle location object
                    location = location3;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                if (marker != null) {
                    marker.remove();
                }

                marker = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Click to teleport here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                );
                marker.showInfoWindow();

            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
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
                    Log.d("SET_MOCK_BEFORE", marker.getPosition().latitude + "--" + marker.getPosition().longitude);

                    mMockGpsProviderTask = new MockGpsProvider();
                    mMockGpsProviderTask.execute(marker.getPosition());
                    marker.remove();

                    if (!isMyServiceRunning(joyStick.class, getApplicationContext())) {
                        startService(new Intent(MapsActivity.this, joyStick.class).putExtra("SCREEN_SIZE", screenWidth).putExtra("SCREEN_DENSITY", deviceDensity));

                        stopBtn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ee) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
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
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false,
                            false, false, true, true, true, 0, 5);
                    Log.d("SET_MOCK_BEFORE", marker.getPosition().latitude + "--" + marker.getPosition().longitude);

                    mMockGpsProviderTask = new MockGpsProvider();
                    mMockGpsProviderTask.execute(marker.getPosition());
                    marker.remove();

                    if (!isMyServiceRunning(joyStick.class, getApplicationContext())) {
                        startService(new Intent(MapsActivity.this, joyStick.class).putExtra("SCREEN_SIZE", screenWidth).putExtra("SCREEN_DENSITY", deviceDensity));

                        stopBtn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ee) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("You need to Select Fake Gps as a Mock location app");
// Add the buttons
                    builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {


                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            } catch (Exception ee) {
                                Toast.makeText(getApplicationContext(), "Please enable Developer option From Settings", Toast.LENGTH_SHORT).show();
                            }
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

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            isSearchOpen = true;
            view.setVisibility(View.VISIBLE);
            //view.findViewById(R.id.place_autocomplete_clear_button).setVisibility(View.GONE);
            view.setBackgroundColor(Color.WHITE);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }
        Log.d("SET_MOCK_LOCATIONCHANGE", location.getLatitude() + "--" + location.getLongitude());
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
                location.setTime(System.currentTimeMillis());
//                location.setBearing(3.14F);
//                location.setSpeed(2);
//                location.setAltitude(10);
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

                locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

                // Thread.sleep(300);

                // sleep for a while before providing next location
            } catch (Exception ee) {
                Log.d("ACTIVITY", ee.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            mMap.clear();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(LOG_TAG, "onProgressUpdate():" + values[0]);
            mMockGpsProviderIndex = values[0];
        }
    }

    @TargetApi(17)
    public void setMockLocation() {
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (location != null) {
            pref.edit().putString(AppConstants.LATITUDE, location.getLatitude() + "").apply();
            pref.edit().putString(AppConstants.LONGITUDE, location.getLongitude() + "").apply();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isSearchOpen){
            isSearchOpen = false;
            view.setVisibility(View.GONE);
            autocompleteFragment.setText("");
        }

    }

    @Override
    public void onBackPressed() {
        if(isSearchOpen){
            isSearchOpen = false;
            view.setVisibility(View.GONE);
            autocompleteFragment.setText("");
        }else{
            super.onBackPressed();
        }
    }
}
