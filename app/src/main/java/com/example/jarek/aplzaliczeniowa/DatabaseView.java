package com.example.jarek.aplzaliczeniowa;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class DatabaseView extends AppCompatActivity {

    private float[] time = new float[255];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view);

        ListView databaseLV = (ListView) findViewById(R.id.databaseLV);
        databaseLV.setAdapter(new PathListAdapter(getLocationsData()));

    }

    /**
     * Adapter do ListView ścieżek
     */
    private class PathListAdapter extends ArrayAdapter<Location[]> {

        PathListAdapter(List<Location[]> locations) {
            super(DatabaseView.this, android.R.layout.simple_list_item_1, locations);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                //layout dla pojedynczych elementow jest domyslny androidowy
                row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                row = convertView;
            }

            //obliczenie odleglosci (androidowe distanceTo oblicza odleglosc miedzy dwoma lokalizacjami)
            float distance = getItem(position)[0].distanceTo(getItem(position)[1]);
            //i predkosci
            float velocity = distance / time[position];

            TextView tv = row.findViewById(android.R.id.text1);
            tv.setText("Distance: " + distance + "m, velocity: " + velocity + "m/s");
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DatabaseView.this, MapsActivity.class);
                    intent.putExtra("location1", getItem(position)[0]);
                    intent.putExtra("location2", getItem(position)[1]);
                    startActivity(intent);
                }
            });
            return row;
        }
    }

    /**
     * @return Zwraca listę par lokalizacji (kazdy element listy posiada dwie lokalizacje - początkową
     * i końcową, oraz czas jaki zajęło przebycie między jednym a drugim.
     * Dane pobierane są z bazy.
     */
    private List<Location[]> getLocationsData() {
        List<Location[]> locations = new LinkedList<>();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();
        Cursor cursor = databaseAdapter.getAllPathData();
        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()) {
            Location location1 = new Location("");
            location1.setLatitude(cursor.getDouble(1));
            location1.setLongitude(cursor.getDouble(2));
            Location location2 = new Location("");
            location2.setLatitude(cursor.getDouble(3));
            location2.setLongitude(cursor.getDouble(4));
            //czas nie moze byc zapisywany do listy lokalizacji, dlatego jest zapisywany do tablicy pola tej klasy
            time[i] = cursor.getFloat(5);

            Location[] locationItems = new Location[2];
            locationItems[0] = location1;
            locationItems[1] = location2;
            locations.add(locationItems);
            cursor.moveToNext();

            i++;
        }
        cursor.close();
        return locations;
    }
}
