package com.micronimbus.doctorshaheb;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<Integer> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.nev_draw);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_home_images);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_home);

        setupImageList();
    }

    private void setupImageList() {
        imageList = new ArrayList<>();
        imageList.add(R.drawable.den);
        imageList.add(R.drawable.hiv);
        imageList.add(R.drawable.mal);
        imageList.add(R.drawable.mot);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Vertical list
        recyclerView.setAdapter(new ImageAdapter());
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_linear, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            int imgRes = imageList.get(position);
            holder.imageView.setImageResource(imgRes);

            holder.imageView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_zoom_image);
                ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
                zoomImage.setImageResource(imgRes);
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.home_image);
            }
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
                Intent intentai = new Intent(this, AIDoctor.class);
                startActivity(intentai);
                break;

            case R.id.nav_doctor_appointment:
                Toast.makeText(this, "Book appoitment selected", Toast.LENGTH_SHORT).show();
                Intent intentbook = new Intent(this, BookAppointmentActivity.class);
                startActivity(intentbook );
                break;

            case R.id.nav_med_doc:
                Toast.makeText(this, "Live Doctor selected", Toast.LENGTH_SHORT).show();
                Intent intentmeddoc = new Intent(this, DocumentsActivity.class);
                startActivity(intentmeddoc);
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
                Toast.makeText(this, "Buy medicine selected", Toast.LENGTH_SHORT).show();
                Intent buyMedicineIntent = new Intent(this, BuyMedicineActivity.class);
                startActivity(buyMedicineIntent);
                break;

            case R.id.nav_add_doctor:
                Toast.makeText(this, "Add Medicine selected", Toast.LENGTH_SHORT).show();
                Intent intentaddmed = new Intent(this, AddDoctorActivity.class);
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
            case R.id.nav_my_appointment:

                Intent myappointment = new Intent(this, MyAppointmentsActivity.class);
                startActivity(myappointment);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else super.onBackPressed();
    }
}
//utsho?00000