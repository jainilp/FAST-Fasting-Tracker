package com.example.fast;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Tips extends AppCompatActivity {

    Button btnSignOut;

    private DrawerLayout dl;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        btnSignOut = findViewById(R.id.signOut);

        dl = (DrawerLayout)findViewById(R.id.dl);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Tips.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
                    Toast.makeText(Tips.this, "My Profile", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.history)
                {
                    openHistory();
                    Toast.makeText(Tips.this, "History", Toast.LENGTH_SHORT).show();

                }
                else if (id == R.id.dashboard)
                {
                    openDashboard();
                    Toast.makeText(Tips.this, "Dashboard", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.signOut){
                    openSignOut();
                    Toast.makeText(Tips.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.fasting_tips){
                    openTips();
                    Toast.makeText(Tips.this, "Fasting Tips!", Toast.LENGTH_SHORT).show();
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
}
