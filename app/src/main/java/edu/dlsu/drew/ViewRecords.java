package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewRecords extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    FirebaseAuth fAuth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> listID = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference().child("Records");

        query.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int x = 0;
                    for(DataSnapshot child: snapshot.getChildren()){

                        List<String> details = new ArrayList<String>();


                        String postId = child.getKey();

                        String name = (String) snapshot.child(postId).child("name").getValue();
                        String longitude = (String) snapshot.child(postId).child("longitude").getValue();
                        String latitude = (String) snapshot.child(postId).child("latitude").getValue();

                        //add dates here
                        String date = (String) snapshot.child(postId).child("date").getValue();
                        listDataHeader.add(name+" "+date);


                        String person = (String) snapshot.child(postId).child("person").getValue();


                        //add ddates here
                        details.add("Location: "+longitude+" , "+latitude+"\nSubmitted by: "+person);

                        Query query3= FirebaseDatabase.getInstance().getReference().child("Records").child(postId).child("Respondents");
                        query3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String respondentListString="Responders: ";
                                ArrayList<String> respondentList = new ArrayList<String>();
                                for (DataSnapshot child : snapshot.getChildren()){

                                    String Respondent = (String) child.getValue();
                                    respondentList.add(Respondent);
                                    respondentListString = respondentListString.concat(Respondent + " ");
                                }

                                System.out.println(respondentListString);
                                details.add(respondentListString);


                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });



                        Query query2 = FirebaseDatabase.getInstance().getReference().child("Records").child(postId).child("Hospitals");
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()){

                                    String name = (String) child.child("name").getValue();

                                    details.add(name);

                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });




                        listDataChild.put(listDataHeader.get(x),details);
                        x++;
                        listAdapter = new ExpandableListAdapter(ViewRecords.this, listDataHeader, listDataChild);

                        // setting list adapter
                        expListView.setAdapter(listAdapter);

                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, android.view.View v, int groupPosition, int childPosition, long id) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        listDataHeader.get(groupPosition)
                                                + " : "
                                                + listDataChild.get(
                                                listDataHeader.get(groupPosition)).get(
                                                childPosition), Toast.LENGTH_SHORT)
                                        .show();
                                return false;
                            }
                        });
                        // Listview Group expanded listener
                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                            @Override
                            public void onGroupExpand(int groupPosition) {
                                Toast.makeText(getApplicationContext(),
                                        listDataHeader.get(groupPosition) + " Expanded",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Listview Group collasped listener
                        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                            @Override
                            public void onGroupCollapse(int groupPosition) {
                                Toast.makeText(getApplicationContext(),
                                        listDataHeader.get(groupPosition) + " Collapsed",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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
            }case R.id.nav_logout: {
                fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}