package edu.dlsu.drew;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Map extends Activity {
    MapView map = null;
    MapController mMapController;
    private DatabaseReference mDatabase,notif;
    // Storage Permissions
    Event event = new Event();
    String disaster, icon;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseDatabase db;
    int id = 0;
    GeoPoint geopoint;

    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        setContentView(R.layout.activity_map);

        map = (MapView) findViewById(R.id.map);
        mMapController = (MapController) map.getController();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        map.setTileSource(TileSourceFactory.MAPNIK);
        GeoPoint startPoint = new GeoPoint(12.8797, 121.7740);
        mMapController.setCenter(startPoint);
        //mMapController.animateTo(startPoint);
        mMapController.setZoom(7);






        Spinner dropdown = findViewById(R.id.spinner1);

        String[] items = new String[]{"Fire", "Earthquake", "Typhoon", "Flood", "Covid","Landslide"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                disaster = dropdown.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());

                geopoint  = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());


               Drawable marker = getApplicationContext().getResources().getDrawable(R.drawable.ic_cloud);
                if (disaster.equals("Fire")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.fire_icon);

                }
                else if (disaster.equals("Covid")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.covid_icon);
                }
                else if (disaster.equals("Flood")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.flood_icon);
                }

                else if (disaster.equals("Landslide")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.landslidewarning_icon);
                }

                else if (disaster.equals("Earthquake")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.earthquake_icon);
                }
                else if (disaster.equals("Typhoon")){
                    marker = getApplicationContext().getResources().getDrawable(R.drawable.typhoon_icon);
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Query queryAuth = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                queryAuth.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                 String namePerson = (String) snapshot.child("name").getValue();
                                                                 event.setPerson(namePerson);
                                                             }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                 String longitude = Double.toString(((double)loc.getLongitudeE6())/1000000);
                 String latitude = Double.toString(((double)loc.getLatitudeE6())/1000000);
                System.out.println("- Latitude = " + latitude + ", Longitude = " + longitude );

                // get the date and time
                long millis=System.currentTimeMillis();
                java.util.Date date=new java.util.Date(millis);
                //setting event to be placed onto firebase
                setEvent(disaster,longitude,latitude,date);



                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
                mapItem.setMarker(marker);
                overlayArray.add(mapItem);
                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();

                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);

                }

                //      dlgThread();
                return true;
            }
        };
        map.getOverlays().add(touchOverlay);
        notif = db.getInstance().getReference().child("Coordinates");
        notif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    id = (int)snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        notif.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                notification();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //gonna put the fire base saving here just put this on an on click in a save button
    public void saveMarker(View view){


    //Event event = new Event(name, longitude, latitude)
        DatabaseReference reference = mDatabase.child("Coordinates").push();
        String newKey = reference.getKey();
        reference.setValue(event);

        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
        ArrayList<POI> pois = poiProvider.getPOICloseTo(geopoint, "Hospital", 15, 0.5);

        for (POI poi:pois) {
            Hospital hospi = new Hospital();
            hospi.setName(poi.mDescription);
            hospi.setLongitude(poi.mLocation);

            mDatabase.child("Coordinates").child(newKey).child("Hospitals").push().setValue(hospi);
        }

        alert("Success!","Marker has been saved to the database.","OK");



}

//this will set the events stuff (global variable ) so that when you click savemarker it just shows the object
    public void setEvent (String name, String longi, String lati,Date date){

        event.setName(name);
        event.setLatitude(lati);
        event.setLongitude(longi);
        event.setDate(date);

}

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
    private void alert(String title, String message, String positiveButton ){
        AlertDialog alert = new AlertDialog.Builder(Map.this).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int x) {
                dialog.dismiss();
            }
        }).create();
        alert.show();
    }
    private void notification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n").setContentText("DREW").setSmallIcon(R.drawable.notification_icon).setAutoCancel(true).setContentText(disaster +" Warning");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_record: {
                Intent intent = new Intent(this, Map.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_account: {
                Intent intent = new Intent(this, AccountOptions.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_home: {
                Intent intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}