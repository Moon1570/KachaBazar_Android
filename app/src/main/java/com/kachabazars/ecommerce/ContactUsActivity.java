package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ContactUsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    public static final String login = "logout";
    public static final String cid = "null";
    String check;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_us);


            sharedPreferences = getSharedPreferences(mypreference,
                    Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.foreground));

            }

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            navigationView.setItemIconTintList(null);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent1;
                    if (menuItem.getTitle().equals("আমাদের বাজার")) {
                        intent1 = new Intent(ContactUsActivity.this, MainActivity.class);
                        startActivity(intent1);
                    }
                    else if (menuItem.getTitle().equals("সব দেখুন")){
                        intent1 = new Intent(ContactUsActivity.this, BrowseAllActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                        intent1 = new Intent(ContactUsActivity.this, SellerActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                        intent1 = new Intent(ContactUsActivity.this, LocalActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                        intent1 = new Intent(ContactUsActivity.this, SearchActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("প্রফাইল")) {
                        intent1 = new Intent(ContactUsActivity.this, ProfileActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                        intent1 = new Intent(ContactUsActivity.this, SellerLoginActivity.class);
                        startActivity(intent1);
                    }

                    else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                        intent1 = new Intent(ContactUsActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                        editor.clear();
                        editor.commit();
                        intent1 = new Intent(ContactUsActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                        intent1 = new Intent(ContactUsActivity.this, ContactUsActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                        intent1 = new Intent(ContactUsActivity.this, AboutUsActivity.class);
                        startActivity(intent1);
                    }
                    return true;
                }
            });
        }
    }