package com.micronimbus.doctorshaheb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
DrawerLayout drawerLayout;
NavigationView navigationView;
Toolbar toolbar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.nev_draw);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        navigationView.bringToFront();

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.nev_draw);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()) {
            case R.id.nav_home:

                break;

            case R.id.nav_about:
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                break;

            case R.id.nav_medicine:
                Toast.makeText(this, "Medicine selected", Toast.LENGTH_SHORT).show();
                Intent intentmed= new Intent(this, MedicineTime.class);
                startActivity(intentmed);
                break;

            case R.id.nav_aidoctor:
                Toast.makeText(this, "AI Doctor selected", Toast.LENGTH_SHORT).show();
                Intent intentai = new Intent(this, Doctor.class);
                startActivity(intentai);
                break;

            case R.id.nav_doctor:
                Toast.makeText(this, "AI Doctor selected", Toast.LENGTH_SHORT).show();
                Intent intentdoc = new Intent(this, Doctor.class);
                startActivity(intentdoc);
                break;

            case R.id.nav_live_doctor:
                Toast.makeText(this, "Live Doctor selected", Toast.LENGTH_SHORT).show();
                Intent intentLI = new Intent(this, LiveDoctor.class);
                startActivity(intentLI);
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
                Intent intentpro = new Intent(this, Profile.class);
                startActivity(intentpro);
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent logoutIntent = new Intent(this, Registration.class);
                startActivity(logoutIntent);
                finish();
                break;

            case R.id.nav_feedback1:
                Toast.makeText(this, "Feedback selected", Toast.LENGTH_SHORT).show();
                Intent feedIntent = new Intent(this, Feedback.class);
                startActivity(feedIntent);
                break;
            case R.id.nav_buy_medicine:
                Toast.makeText(this, "Feedback selected", Toast.LENGTH_SHORT).show();
                Intent buyMedicineIntent = new Intent(this, BuyMedicineActivity.class);
                startActivity(buyMedicineIntent);
                break;

            case R.id.nav_add_medicine:
                Toast.makeText(this, "Add Medicine selected", Toast.LENGTH_SHORT).show();
                Intent intentaddmed = new Intent(this, AddMedicineActivity.class);
                startActivity(intentaddmed);
                break;
            case R.id.nav_blood:
                Toast.makeText(this, "Angel blood selected", Toast.LENGTH_SHORT).show();
                Intent blood = new Intent(this, Blood.class);
                startActivity(blood);
                break;
            case R.id.nav_Test:
                Toast.makeText(this, "Angel blood selected", Toast.LENGTH_SHORT).show();
                Intent test = new Intent(this, MainActivity.class);
                startActivity(test);
                break;



        }
drawerLayout.closeDrawer(GravityCompat.START);


        return true;
    }
}