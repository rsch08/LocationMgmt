package com.mycompany.locationmgmt;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {
    private TextView latitude;
    private TextView longitude;
    private TextView speed, distance;
    private TextView choice;
    private CheckBox fineAcc;
    private Button choose, start, stop, update, exit;
    private TextView provText;
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    private Intent mServiceIntent;
    private Location location;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        provText = (TextView) findViewById(R.id.prov);
        choice = (TextView) findViewById(R.id.choice);
        fineAcc = (CheckBox) findViewById(R.id.fineAccuracy);
        choose = (Button) findViewById(R.id.chooseRadio);
        start = (Button) findViewById(R.id.start);
        //mServiceIntent= new Intent(getApplicationContext(), MyService.class);


        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);	//default



        // user defines the criteria
        choose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fineAcc.isChecked()){
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    choice.setText("fine accuracy selected");
                    //startService(mServiceIntent);
                }else {
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    choice.setText("coarse accuracy selected");
                }
            }
        });


        //TODO Start services
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creates IntentService
                //mServiceIntent= new Intent(getApplicationContext(), service.class);
                //Starts service
                //startService(mServiceIntent);

            }
        });

        //TODO Stop services
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopService(mServiceIntent);
            }
        });

        //TODO Update values
        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(mServiceIntent);
            }
        });



        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        location = locationManager.getLastKnownLocation(provider);

        mylistener = new MyLocationListener();

        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
            speed.setText("Speed: " + String.valueOf(location.getSpeed()));
            //distance.setText("Distance: " + String.valueOf(location.g));
            provText.setText(provider + " provider has been selected.");

            Toast.makeText(MainActivity.this,  "Location changed!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class MyService extends IntentService{
        //Required constructor
        public MyService(){
            super("MyService");
        }

        //Service handler
        @Override
        protected void onHandleIntent(Intent intent) {
            try{
                File sdDir = Environment.getExternalStorageDirectory();
                File logFile = new File(sdDir, "log.txt");
                if(!logFile.exists()){
                    logFile.mkdirs();
                }
                FileOutputStream file = openFileOutput("log.txt", MODE_WORLD_WRITEABLE);
                OutputStreamWriter output = new OutputStreamWriter(file);
                output.write("Longitude = " + String.valueOf(location.getLongitude()) + "\n");
                output.write("Latitude = " + String.valueOf(location.getLatitude()) + "\n");
                output.write("Speed = " + String.valueOf(location.getSpeed()) + "\n");
                output.close();

                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();


            }
            catch (IOException e){

            }

        }
    }
}