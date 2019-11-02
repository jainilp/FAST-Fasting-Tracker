package com.example.fast;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class hist extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button btnSignOut;

    private TextView log;
    private String email;
    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "hist";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("ResourceType")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist);

        btnSignOut = findViewById(R.id.signOut);

        dl = (DrawerLayout)findViewById(R.id.dl);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(hist.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email = user.getEmail();
        log();


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
                    Toast.makeText(hist.this, "My Profile", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.history)
                {
                    openHistory();
                    Toast.makeText(hist.this, "History", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.dashboard)
                {
                    openDashboard();
                    Toast.makeText(hist.this, "Dashboard", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.signOut){
                    openSignOut();
                    Toast.makeText(hist.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.fasting_tips){
                    openTips();
                    Toast.makeText(hist.this, "Fasting Tips!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void openSignOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openDashboard(){
        Intent intent = new Intent(this, HomePage.class);
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

    public void log() {
        log = findViewById(R.id.histLog);

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
                                    data += document.get("Fasting Time");

                                }
                            }
                            data = data.replace("["," ");
                            data = data.replace("]"," ");
                            data = data.replace(",", "\n");
                            log.setText(data);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    };

    });
    }
}
