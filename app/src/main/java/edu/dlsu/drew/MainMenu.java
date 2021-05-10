package edu.dlsu.drew;

import androidx.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

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
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_cloud));
        startMarker.setTitle("Typhoon here");



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

        xylist[0][0]=12.8797;
        xylist[0][1]=122.7740;


        //xylist[i][j] = event.getlongitude()

        xylist[1][0]=11.8797;
        xylist[1][1]=122.7740;

        xylist[2][0]=10.8797;
        xylist[2][1]=122.7740;

        xylist[3][0]=9.8797;
        xylist[3][1]=122.7740;

        xylist[4][0]=13.8797;
        xylist[4][1]=122.7740;


//find a way to make a counter
        for(int i =0 ; i<xylist.length;i++)
        {
            if (xylist[i][0] == 0 && xylist[i][1]==0){
                break;
            }else{
                GeoPoint geoPoint = new GeoPoint(xylist[i][0],xylist[i][1]);
                geoPoints.add(geoPoint);
                //System.out.println(i);
            }
        }

        //System.out.println(geoPoints);

        for(int i =0 ; i<geoPoints.size();i++)
        {
            Marker marker = new Marker(map);
            marker.setPosition(geoPoints.get(i));
            System.out.println(i);

            //get the longitude and latitude to make a geopoint permanent
            GeoPoint scanGeoPoint = new GeoPoint(xylist[i][0],xylist[i][1]);


            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    //if (!clicked){

                    marker.showInfoWindow();
                    mapView.getController().animateTo(marker.getPosition());
                    NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
                    ArrayList<POI> pois = poiProvider.getPOICloseTo(scanGeoPoint, "Hospital", 25, 0.5);
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
            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_cloud));
            startMarker.setTitle("Typhoon here");
            map.getOverlays().add(marker);
        }
        map.invalidate();

    }

//this will be the function to get the longitude and latitude and return a double 2d array
    public double [][] getData(){
        double [][] xylist = new double[20][20];



        return xylist;

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