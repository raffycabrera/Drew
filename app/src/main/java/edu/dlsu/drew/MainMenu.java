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
import android.widget.Button;
import android.widget.Toast;

import androidx.core.view.GravityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MapView map = null;
    MapController mMapController;

    private DatabaseReference mDatabase;
    Button delete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        delete = findViewById(R.id.deleteButton);

        delete.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();

        navigationView.setItemIconTintList(null);

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

        mMapController.setZoom(7);


        List<GeoPoint> geoPoints = new ArrayList<>();
        double [][] xylist = new double[20][20];


        List<String> listID = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Coordinates");

        Query query = FirebaseDatabase.getInstance().getReference().child("Coordinates");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query queryAuth = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

       queryAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = (String) snapshot.child("role").getValue();
                System.out.println(role);



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


                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                    String name = (String) dataSnapshot.child(mPostId).child("name").getValue();
                                                    String longitude = (String) dataSnapshot.child(mPostId).child("longitude").getValue();
                                                    String latitude = (String) dataSnapshot.child(mPostId).child("latitude").getValue();

                                                    String person = (String) dataSnapshot.child(mPostId).child("person").getValue();




                                                    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                                    Marker marker = new Marker(map);
                                                    marker.setPosition(geoPoint);
                                                    marker.setSnippet("Submitted by: "+person);

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


                                                            marker.showInfoWindow();
                                                            mapView.getController().animateTo(marker.getPosition());
                                                            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
                                                            ArrayList<POI> pois = poiProvider.getPOICloseTo(geoPoint, "Hospital", 15, 0.5);
                                                            FolderOverlay poiMarkers = new FolderOverlay(getApplicationContext());
                                                            map.getOverlays().add(poiMarkers);
                                                            Drawable poiIcon = getResources().getDrawable(R.drawable.hospital_icon);
                                                            for (POI poi:pois){
                                                                Marker poiMarker = new Marker(map);


                                                                poiMarker.setTitle("Hospital");
                                                                poiMarker.setSnippet(poi.mDescription);
                                                                poiMarker.setPosition(poi.mLocation);



                                                                poiMarker.setIcon(poiIcon);
                                                                //make a new child under each event that holds hospital locations

                                                                poiMarkers.add(poiMarker);

                                                                marker.setPosition(geoPoint);
                                                            }
                                                            long speed = 3;
                                                            mMapController.animateTo(startPoint,9.00,speed);

                                                            if (role.equals("Responder")){

                                                                delete.setVisibility(View.VISIBLE);
                                                                delete.setText("Respond");
                                                                delete.setOnClickListener(new View.OnClickListener() {
                                                                                              @Override
                                                                                              public void onClick(View v) {

                                                                                                  String respondentname = (String) snapshot.child("name").getValue();

                                                                                                  Query queryR = FirebaseDatabase.getInstance().getReference().child("Coordinates").child(postId).child("Respondents");

                                                                                                  queryR.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                      @Override
                                                                                                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                          if(snapshot.exists()) {
                                                                                                              for (DataSnapshot child : snapshot.getChildren()) {


                                                                                                                  if (child.getValue().equals(respondentname)) {
                                                                                                                      delete.setVisibility(View.GONE);

                                                                                                                  } else {
                                                                                                                      mDatabase.child(postId).child("Respondents").push().setValue(respondentname);
                                                                                                                      delete.setVisibility(View.GONE);
                                                                                                                  }


                                                                                                              }
                                                                                                          }else {
                                                                                                              mDatabase.child(postId).child("Respondents").push().setValue(respondentname);
                                                                                                              delete.setVisibility(View.GONE);
                                                                                                          }




                                                                                                      }
                                                                                                      @Override
                                                                                                      public void onCancelled(@NonNull DatabaseError error) {

                                                                                                      }


                                                                                                  });





                                                                                              }
                                                                                          }



                                                                );


                                                                                          }


                                                            if (role.equals("Administrator")){
                                                                delete.setVisibility(View.VISIBLE);


                                                                delete.setOnClickListener(new View.OnClickListener() {
                                                                    public void onClick(View v) {

                                                                        mDatabase = FirebaseDatabase.getInstance().getReference();
                                                                        Event userEvent = new Event();


                                                                        long millis=System.currentTimeMillis();
                                                                        java.util.Date date=new java.util.Date(millis);
                                                                        Calendar calendar = Calendar.getInstance();
                                                                        calendar.setTime(date);

                                                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                                                                        int month = calendar.get(Calendar.MONTH)+1;
                                                                        int year = calendar.get(Calendar.YEAR);


                                                                        String dateString = (day+"/"+month+"/"+year);
                                                                        userEvent.setName(name);
                                                                        userEvent.setLongitude(longitude);
                                                                        userEvent.setLatitude(latitude);
                                                                        userEvent.setDate(dateString);
                                                                        userEvent.setPerson(person);


                                                                        mDatabase.child("Records").child(postId).setValue(userEvent);


                                                                        //remove data child from the database i think you can jusit take the uid and delete it

                                                                        //get the hospitals and move it to the new one
                                                                        //do the same thing for the list of responders


                                                                        Query query2 = FirebaseDatabase.getInstance().getReference().child("Coordinates").child(postId).child("Hospitals");
                                                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot child : snapshot.getChildren()){
                                                                                    System.out.println("this works");
                                                                                    Hospital hospi = new Hospital();

                                                                                    String name = (String) child.child("name").getValue();


                                                                                    //HashMap longitude1 = (HashMap) child.child("longitude").getValue();

                                                                                    hospi.setName(name);
                                                                                    mDatabase.child("Records").child(postId).child("Hospitals").push().setValue(hospi);

                                                                                }

                                                                            }
                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                            }


                                                                        });
                                                                        Query query3 = FirebaseDatabase.getInstance().getReference().child("Coordinates").child(postId).child("Respondents");
                                                                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot child : snapshot.getChildren()){

                                                                                    String name = (String) child.getValue();




                                                                                    mDatabase.child("Records").child(postId).child("Respondents").push().setValue(name);

                                                                                }

                                                                            }
                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }


                                                                        });

                                                                        mDatabase.child("Coordinates").child(postId).removeValue();
                                                                        delete.setVisibility(View.GONE);


                                                                        InfoWindow.closeAllInfoWindowsOn(map);
                                                                        map.getOverlays().remove(marker);
                                                                        map.getOverlays().remove(poiMarkers);

                                                                        mapView.invalidate();
                                                                    }
                                                                });


                                                            }



                                                            // }

                                                            return true;
                                                        }});

                                                    map.getOverlays().add(marker);
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
            public void onCancelled(@NonNull DatabaseError error) {

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
            case R.id.nav_account: {
                Intent intent = new Intent(this, AccountOptions.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_home: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }case R.id.nav_viewRecord: {
                Intent intent = new Intent(this, ViewRecords.class);
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