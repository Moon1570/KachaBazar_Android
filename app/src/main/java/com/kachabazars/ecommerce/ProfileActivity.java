package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kachabazars.ecommerce.config.IpConfig;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

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
    private CircleImageView circleImageView;
    private TextView name, phone, division, district;

    IpConfig ipConfig = new IpConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if (sharedPreferences.getString("login", null) == null)
        {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.foreground));

            }

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            name = findViewById(R.id.profile_Name);
            phone = findViewById(R.id.profile_phone);
            division = findViewById(R.id.profile_division);
            district = findViewById(R.id.profile_district);


            menu = navigationView.getMenu();
            menuItem1 = menu.findItem(R.id.nav_login);
            if (sharedPreferences.getString("login", null) ==null)
            {
                ///Do Nothing
            }
            else {
                menuItem1.setTitle("লগ আউট করুন");
            }


            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            circleImageView = findViewById(R.id.profile_picture);

            navigationView.setItemIconTintList(null);


            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();







            String customer = sharedPreferences.getString("cid", null);
            try {
                JSONObject jsonObject = new JSONObject(customer);
                String image=jsonObject.getString("customerImageName");
                byte[] imageByte;

                imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
                Bitmap bmp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);

                circleImageView.setImageBitmap(bmp);
          //      name.setText(jsonObject.getString("customerFirstName") + " " + jsonObject.getString("customerLastName"));
           //     phone.setText(jsonObject.getString("customerPhone"));

                name.setText("This frature is coming soon");

                String area = jsonObject.getString("divisionmodel");
                JSONObject jsonObject1 = new JSONObject(area);
//                division.setText(jsonObject1.getString("divisionName"));

                area = jsonObject.getString("districtModel");
                jsonObject1 = new JSONObject(area);
//                district.setText(jsonObject1.getString("districtName"));


            } catch (JSONException e) {
                e.printStackTrace();
            }






            menu = navigationView.getMenu();
            menuItem1 = menu.findItem(R.id.nav_login);

            if (sharedPreferences.getString("login", null) == "login")
            {
                menuItem1.setTitle("Logout");
            }





            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent1;
                    if (menuItem.getTitle().equals("আমাদের বাজার")) {
                        intent1 = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent1);
                    }
                    else if (menuItem.getTitle().equals("সব দেখুন")){
                        intent1 = new Intent(ProfileActivity.this, BrowseAllActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                        intent1 = new Intent(ProfileActivity.this, SellerActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                        intent1 = new Intent(ProfileActivity.this, LocalActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                        intent1 = new Intent(ProfileActivity.this, SearchActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("প্রফাইল")) {
                        intent1 = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                        intent1 = new Intent(ProfileActivity.this, SellerLoginActivity.class);
                        startActivity(intent1);
                    }

                    else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                        intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                        editor.clear();
                        editor.commit();
                        intent1 = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                        intent1 = new Intent(ProfileActivity.this, ContactUsActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                        intent1 = new Intent(ProfileActivity.this, AboutUsActivity.class);
                        startActivity(intent1);
                    }
                    return true;
                }
            });
        }


    }
}
