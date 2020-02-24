package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kachabazars.ecommerce.config.IpConfig;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CartActivity extends AppCompatActivity {

    double total = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    public static final String login = "logout";
    String check;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    ListView listView;
    Button btnCheckout, btncontinueToShopping;
    TextView carttotal;

    IpConfig ipConfig =  new IpConfig();
    String MYURL = ipConfig.myURI + "restcart";

    RequestParams params;
    AsyncHttpClient client;

    String id;
    Context context;

    CartListAdapter cartListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("login", null) == null)
        {
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
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

            listView = findViewById(R.id.cart_list);
            btnCheckout = findViewById(R.id.cart_checkout);
            carttotal = findViewById(R.id.cart_total);
            btncontinueToShopping = findViewById(R.id.btn_continue_to_shopping);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            navigationView.setItemIconTintList(null);


            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            menu = navigationView.getMenu();
            menuItem1 = menu.findItem(R.id.nav_login);
            if (sharedPreferences.getString("login", null) ==null)
            {
                ///Do Nothing
            }
            else {
                menuItem1.setTitle("লগ আউট করুন");
            }


            id = sharedPreferences.getString("id", null);

            params = new RequestParams();
            client =  new AsyncHttpClient();

            params.put("action", "getcartdetails");
            params.put("cid", id);

            client.get(MYURL, params, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                    try {

                        cartListAdapter = new CartListAdapter(context, R.layout.cart_row_layout);
                        listView.setAdapter(cartListAdapter);


                        int count = 0;

                        int a =1;
                        String sl;

                        String name, price, stotal;

                        while (count<response.length())
                        {
                            JSONObject JO = response.getJSONObject(count);
                            name = JO.getString("productName");
                            price = JO.getString("price");
                            stotal = JO.getString("stotal");

                            total = total + Double.parseDouble(stotal);

                            sl = a+"";
                            CartView cartView = new CartView(sl, name, price, stotal);


                            cartListAdapter.add(cartView);


                            a++;
                            count++;
                        }

                        carttotal.setText("Total " +total+" tk only");




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

            btncontinueToShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CartActivity.this, CartOrderActivity.class);
                    intent.putExtra("total", total);
                    startActivity(intent);
                }
            });

        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(CartActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(CartActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(CartActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(CartActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(CartActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(CartActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(CartActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(CartActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(CartActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });


    }




    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

}
