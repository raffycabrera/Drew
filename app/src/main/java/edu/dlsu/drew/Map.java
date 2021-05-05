package edu.dlsu.drew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class Map extends Activity {
    MapView map = null;
    MapController mMapController;
    // Storage Permissions


    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        //Context ctx = getApplicationContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_map);

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

//get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
//create a list of items for the spinner.
        String[] items = new String[]{"Fire", "Earthquake", "Typhoon", "Flood"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                final Drawable marker = getApplicationContext().getResources().getDrawable(R.drawable.ic_cloud);
                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                String longitude = Double.toString(((double)loc.getLongitudeE6())/1000000);
                String latitude = Double.toString(((double)loc.getLatitudeE6())/1000000);
                System.out.println("- Latitude = " + latitude + ", Longitude = " + longitude );
                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
                mapItem.setMarker(marker);
                overlayArray.add(mapItem);
                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    System.out.println(longitude);
                    System.out.println(latitude);
                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    System.out.println(longitude);
                    System.out.println(latitude);
                }
                //      dlgThread();
                return true;
            }
        };
        map.getOverlays().add(touchOverlay);



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
}