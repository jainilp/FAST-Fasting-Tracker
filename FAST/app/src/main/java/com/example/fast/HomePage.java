package com.example.fast;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomePage extends AppCompatActivity {

    Button btnstart, btnstop;
    ImageView icanchor;
    Animation roundingalone;
    Chronometer timer;
    Button btnSignOut;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView displayName;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "HomePage";
    private Chronometer timerTV;

    private String email;
    public String name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @SuppressLint("ResourceType")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    //Initialize home_page activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);    //Sets the page of the activity

        //Initialize variables to associated view
        btnstart = findViewById(R.id.btnstart);     //Button for starting the fast
        btnstop = findViewById(R.id.btnstop);       //Button for ending the fast
        icanchor = findViewById(R.id.icanchor);     //Arrow that goes around the circle when timer is started
        roundingalone = AnimationUtils.loadAnimation(this, R.layout.roundingalone);
        timer = findViewById(R.id.timer);
        displayName = findViewById( R.id.displayName);
        btnSignOut = findViewById(R.id.signOut);
        FirebaseFirestore firestore;

        firebaseAuth = FirebaseAuth.getInstance();      //Instantiate the firebase authentication object
        firebaseUser = firebaseAuth.getCurrentUser();   //Instantiate an object for the user that is signed in
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        firestore.setFirestoreSettings(settings);
        email = user.getEmail();
        log();

        dl = (DrawerLayout)findViewById(R.id.dl);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);


        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                icanchor.startAnimation(roundingalone);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icanchor.clearAnimation();

                timer.stop();

                String timerIn;

                timerTV = findViewById(R.id.timer);

                timerIn = timerTV.getText().toString();

                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                String now = format.format(today);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                firestore.setFirestoreSettings(settings);

                db.collection("users").document(email)
                        .update("Fasting Time", FieldValue.arrayUnion("Date: " +now+ " - Fast Length: " + timerIn))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

            }
        });

        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        final NavigationView nav_view = findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.my_profile)
                {
                    Toast.makeText(HomePage.this, "My Profile", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.history)
                {
                    openHistory();
                    Toast.makeText(HomePage.this, "History", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.dashboard)
                {
                    Toast.makeText(HomePage.this, "Dashboard", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.signOut){
                    openSignOut();
                    Toast.makeText(HomePage.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.fasting_tips){
                    openTips();
                    Toast.makeText(HomePage.this, "Fasting Tips!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }


    public void log() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String data = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (email.equals(document.getId())) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    data += document.get("First Name");

                                }
                            }
                            displayName.setText("Welcome " + data + "!");

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }

                });
    }


    public void openSignOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openHistory() {
        Intent intent = new Intent(this, hist.class);
        startActivity(intent);
    }

    public void openTips(){
        Intent intent = new Intent(this, Tips.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}