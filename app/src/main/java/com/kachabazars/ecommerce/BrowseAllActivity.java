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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kachabazars.ecommerce.config.IpConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import cz.msebera.android.httpclient.Header;

public class BrowseAllActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    public static final String mypreference = "mypref";

    IpConfig ipConfig = new IpConfig();

    String check;

    Menu menu;

    FloatingActionButton floatingActionButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    String productString;
    JSONArray jsonArray;
    JSONArray jsonArray2;
    JSONObject jsonObject;
    ProductAdapter productAdapter;
    CategoryAdapter categoryAdapter;
    ListView listView;
    RequestParams params;
    AsyncHttpClient client;
    String MYURL = ipConfig.myURI +"rest";
    String category;
    int catId;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_all);


        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.product_list);
        floatingActionButton = findViewById(R.id.fab_cart);
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

        menu = navigationView.getMenu();
        menuItem1 = menu.findItem(R.id.nav_login);
        if (sharedPreferences.getString("login", null) ==null)
        {
            floatingActionButton.hide();
            ///Do Nothing
        }
        else {
            floatingActionButton.show();
            menuItem1.setTitle("লগ আউট করুন");

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BrowseAllActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            });

            try {
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("cid", null));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        final Intent intent = getIntent();
        if (intent.hasExtra("category")){
            if (intent.getExtras().getString("category") != null){
                category = intent.getExtras().getString("category");
                catId = intent.getExtras().getInt("catId");

                params = new RequestParams();
                params.put("action", "getProductByCategoryId");
                params.put("catId", catId);
                client = new AsyncHttpClient();


                client.get(MYURL, params, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);


                        try {
                            jsonArray = response;
                            productAdapter = new ProductAdapter(context, R.layout.row_layout);
                            listView.setAdapter(productAdapter);

                            int count = 0;
                            String name, price, govtPrice, image;
                            byte[] imageByte;

                            while (count<jsonArray.length())
                            {


                                JSONObject JO = jsonArray.getJSONObject(count);



                                name = JO.getString("productName");
                                price = JO.getString("productPrice");
                                govtPrice = JO.getString("govtPrice");
                                image = JO.getString("productImageName");

                                imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);


                                // Create the decompressor and give it the data to compress
                                Inflater decompressor = new Inflater();
                                decompressor.setInput(imageByte);

                                // Create an expandable byte array to hold the decompressed data
                                ByteArrayOutputStream bos = new ByteArrayOutputStream(imageByte.length);

                                // Decompress the data
                                byte[] buf = new byte[1024];
                                while (!decompressor.finished()) {
                                    try {
                                        int cout = decompressor.inflate(buf);
                                        bos.write(buf, 0, cout);
                                    } catch (DataFormatException e) {
                                    }
                                }
                                try {
                                    bos.close();
                                } catch (IOException e) {
                                }

                                // Get the decompressed data
                                byte[] decompressedData = bos.toByteArray();

                                Bitmap bmp= BitmapFactory.decodeByteArray(decompressedData,0,imageByte.length);
                                Product product = new Product(name, price, govtPrice, bmp);



                                productAdapter.add(product);

                                count++;
                            }

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(BrowseAllActivity.this, ViewProductActivity.class);
                                    intent.putExtra("position", position+"");
                                    try {
                                        String productDetails = jsonArray.getJSONObject(position).toString();
                                        intent.putExtra("product", productDetails);

                                        startActivity(intent);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    // Or / And

                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(BrowseAllActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });

            }
            else {
                Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
            }
        }










        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(BrowseAllActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(BrowseAllActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(BrowseAllActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(BrowseAllActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(BrowseAllActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(BrowseAllActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(BrowseAllActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(BrowseAllActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(BrowseAllActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(BrowseAllActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(BrowseAllActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });



    }
}
