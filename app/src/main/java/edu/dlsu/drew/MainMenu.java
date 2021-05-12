package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MapView map = null;
    MapController mMapController;
    boolean clicked = false;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) findViewById(R.id.map);
        mMapController = (MapController) map.getController();




        map.setTileSource(TileSourceFactory.MAPNIK);
        GeoPoint startPoint = new GeoPoint(12.8797, 121.7740);
        mMapController.setCenter(startPoint);
        //mMapController.animateTo(startPoint);
        mMapController.setZoom(7);

        Marker startMarker = new Marker(map);




        //this is the first option it has a function that draws markers
        /**
         public void addMarker(GeoPoint center) {
         MarkerOptions options = new MarkerOptions();
         Marker marker = new Marker(map);
         options.position(center);
         options.anchor( Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

         Marker marker = map.AddMarker(options);
         }try {
         while (cursor.moveToNext()) {
         GeoPoint point = new GeoPoint(cursor.getLong("lat"), cursor.getLong("lng"));
         addMarker(point);
         }
         } finally {
         cursor.close();
         }
         **/

        List<GeoPoint> geoPoints = new ArrayList<>();
        double [][] xylist = new double[20][20];


        //now find a way to get the longitude and lat from firebase
        //instead of hard coding make this onto a nested for loop


        //put the data listener thing here in the ondatachange have it call the functions that need the data
        //that means youll have to make a function that makes the markers
        //i guess you will have to find another way to hide all the other marker

        List<String> listID = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Coordinates");


        Query query = FirebaseDatabase.getInstance().getReference().child("Coordinates");

        //query.orderByChild("Coordinates").equalTo("test");//Here replace title with your key which you want and replace test with value throw which search
        //query.orderByChild("title").equalTo("test").limitToFirst(1); //If you want to only one value
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //you get here the list of post from  DB
                if (dataSnapshot.exists()) {
                    int i =0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        String postId = child.getKey(); //Post Id
                        //add it here onto a list
                        listID.add(postId);
                        i++;


                        List<Event> list = new ArrayList<>();
                        for (String mPostId : listID) {

                            Query query1 = FirebaseDatabase.getInstance().getReference().child("Coordinates");

                            System.out.println(mPostId);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //you get here the list of post from  DB
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            String name = (String) dataSnapshot.child(mPostId).child("name").getValue();
                                            String longitude = (String) dataSnapshot.child(mPostId).child("longitude").getValue();
                                            String latitude = (String) dataSnapshot.child(mPostId).child("latitude").getValue();


                                            System.out.println(name);
                                            System.out.println(longitude);
                                            System.out.println(latitude);



                                            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                            Marker marker = new Marker(map);
                                            marker.setPosition(geoPoint);


                                            if (name.equals("Fire")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.fire_icon));
                                                marker.setTitle("Fire");

                                            }
                                            else if (name.equals("Covid")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.covid_icon));
                                                marker.setTitle("COVID");
                                            }
                                            else if (name.equals("Flood")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.flood_icon));
                                                marker.setTitle("Flood");
                                            }

                                            else if (name.equals("Landslide")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.landslidewarning_icon));
                                                marker.setTitle("Landslide");
                                            }

                                            else if (name.equals("Earthquake")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.earthquake_icon));
                                                marker.setTitle("Earthquake");
                                            }
                                            else if (name.equals("Typhoon")){
                                                marker.setIcon(getResources().getDrawable(R.drawable.typhoon_icon));
                                                marker.setTitle("Typhoon");
                                            }





                                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker, MapView mapView) {

                                                    //if (!clicked){

                                                    marker.showInfoWindow();
                                                    mapView.getController().animateTo(marker.getPosition());
                                                    NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
                                                    ArrayList<POI> pois = poiProvider.getPOICloseTo(geoPoint, "Hospital", 15, 0.5);
                                                    FolderOverlay poiMarkers = new FolderOverlay(getApplicationContext());
                                                    map.getOverlays().add(poiMarkers);
                                                    Drawable poiIcon = getResources().getDrawable(R.drawable.hospital_icon);
                                                    for (POI poi:pois){
                                                        Marker poiMarker = new Marker(map);
                                                        poiMarker.setTitle(poi.mType);
                                                        poiMarker.setSnippet(poi.mDescription);
                                                        poiMarker.setPosition(poi.mLocation);
                                                        poiMarker.setIcon(poiIcon);

                                                        poiMarkers.add(poiMarker);
                                                        marker.remove(map);
                                                        marker.setPosition(geoPoint);
                                                    }
                                                    long speed = 3;
                                                    mMapController.animateTo(startPoint,9.00,speed);


                                                    // }
                                                    /**
                                                     else {
                                                     mMapController.setCenter(startPoint);
                                                     mMapController.setZoom(7);
                                                     //map.getOverlays().clear();

                                                     }
                                                     **/
                                                    return true;
                                                }});
                                            //have some if statements here depending on what type of calamity it is
                                            map.getOverlays().add(marker);
                                            //trying to put marker on top

                                        map.invalidate();


                                        Event userEvent = new Event();
                                            userEvent.setName(name);
                                            userEvent.setLongitude(longitude);
                                            userEvent.setLatitude(latitude);


                                            list.add(userEvent);

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    @Override
    public void onBackPressed(){

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_record: {
                Intent intent = new Intent(this, Map.class);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


}