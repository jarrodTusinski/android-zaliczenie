package com.example.jarek.aplzaliczeniowa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Location currentLocation;
    private Location startLocation;
    private Location endLocation;

    private ImageButton startPath;
    private ImageButton endPath;
    private TextView start;
    private TextView end;
    private ImageButton addToDatabase;
    private ImageButton goToDatabase;
    private TextView stopwatchTV;
    private TextView currentLocTV;
    private ImageButton button1;
    private ImageButton button2;

    private Stopwatch stopwatch;
    private Double secs;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startPath = (ImageButton) findViewById(R.id.path_start);
        endPath = (ImageButton) findViewById(R.id.path_end);
        start = (TextView) findViewById(R.id.start_coords);
        end = (TextView) findViewById(R.id.end_coords);
        addToDatabase = (ImageButton) findViewById(R.id.add);
        goToDatabase = (ImageButton) findViewById(R.id.database);
        stopwatchTV = (TextView) findViewById(R.id.stopwatch);
        currentLocTV = (TextView) findViewById(R.id.currentLoc);

        //sprawdza czy uzytkownik nadal uprawnienia aplikacji
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Do działania tej funkcji musisz zaznaczyć uprawnienie do używania GPS przez aplikację",Toast.LENGTH_LONG).show();
        }
        else {
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    currentLocation = location;
                    currentLocTV.setText(currentLocation.getLatitude() + ",\n"+ currentLocation.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            startPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setEnabled(false);
                    startLocation = currentLocation;
                    start.setText(startLocation.getLatitude() + ",\n"+ startLocation.getLongitude());
                    end.setText("");
                    endPath.setEnabled(true);
                    addToDatabase.setVisibility(View.INVISIBLE);
                    stopwatchTV.setText("");
                    stopwatch = new Stopwatch();
                    stopwatch.start();
                }
            });

            endPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setEnabled(false);
                    endLocation = currentLocation;
                    end.setText(endLocation.getLatitude() + ",\n"+ endLocation.getLongitude());
                    startPath.setEnabled(true);
                    addToDatabase.setVisibility(View.VISIBLE);
                    stopwatch.stop();
                    secs = stopwatch.getElapsedTime() / 1000.0;
                    stopwatchTV.setText(secs.toString());
                }
            });

            addToDatabase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.INVISIBLE);
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(MainActivity.this);
                    databaseAdapter.open();
                    databaseAdapter.addPath(startLocation.getLatitude(), startLocation.getLongitude(),
                            endLocation.getLatitude(), endLocation.getLongitude(), secs);
                    Toast.makeText(MainActivity.this, "Dodano miejsca do bazy danych.", Toast.LENGTH_SHORT).show();
                    databaseAdapter.close();
                }
            });

            goToDatabase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DatabaseView.class);
                    startActivity(intent);
                }
            });
        }

        // https://stackoverflow.com/questions/21884181/turn-off-screen-programmatically-when-face-is-close-the-screen-on-android
        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

        button1 = (ImageButton) findViewById(R.id.button1);
        button2 = (ImageButton) findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
        });

    }
}
