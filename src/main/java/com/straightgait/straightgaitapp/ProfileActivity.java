package com.straightgait.straightgaitapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG =  "TAG";
    private FirebaseAuth firebaseAuth;
    private TextView textViewWellcome;
    private Button buttonLogOut;
    private DrawerLayout drawer;

    private FirebaseFirestore db;
    private String userId, userName, gender;
    private static DeviceFragment deviceFragment;
    private Date lastConnect;
    private Timestamp lastDateTS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
//        textViewWellcome = (TextView) findViewById(R.id.textViewWellcome);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginAcitivty.class));
        }


        userId = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = db.collection("users").document(userId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    userName = documentSnapshot.getString("firstName");
                    gender = documentSnapshot.getString("gender");
                    lastDateTS = documentSnapshot.getTimestamp("nextLastDate");
                    Log.d(TAG, lastDateTS.toString());

                    lastConnect = lastDateTS.toDate();
                    documentReference.update("lastDate", lastConnect);
                    documentReference.update("nextLastDate", new Date());

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View header = navigationView.getHeaderView(0);
                    TextView textViewHelloMessage = (TextView)header.findViewById(R.id.textViewHelloMessage); //get a reference to the textview on the nav_header.xml file.
                    ImageView imageViewUser = (ImageView)header.findViewById(R.id.imageViewUser);
                    textViewHelloMessage.setText("Hi "+ userName);
                    if(gender.equals("female")){
                        imageViewUser.setImageResource(R.drawable.icon_female);
                    }else if(gender.equals("male")){
                        imageViewUser.setImageResource(R.drawable.icon_male);
                    }
                }else {
                    Toast.makeText(ProfileActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });


//        lastConnect = lastDateTS.toDate();
//        documentReference.update("lastDate", lastConnect);
//        documentReference.update("nextLastDate", new Date());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_statistics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new StatisticsFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_helpCenter:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new HelpCenterFragment()).commit();
                break;
            case R.id.nav_connectBluetooth:
                if(deviceFragment ==null){
                    this.deviceFragment = new DeviceFragment();

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, this.deviceFragment).commit();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragmant_container, new DeviceFragment()).commit();
                break;
            case R.id.nav_logOut:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}
