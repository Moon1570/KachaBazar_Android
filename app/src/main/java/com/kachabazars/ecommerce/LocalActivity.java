package com.kachabazars.ecommerce;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class LocalActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    public static final String login = "logout";
    String check;
    FloatingActionButton floatingActionButton;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TextView name, test;
    ImageView imageView;
    Button btnUnion, btnDistrict, btnUpazilla;
    RequestParams params;
    AsyncHttpClient client;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "restlocal";

    JSONArray jsonArray;
    JSONArray jsonArray2;
    JSONObject jsonObject;
    SellerProductAdapter sellerProductAdapter;
    ListView listView;

    int select = 0;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        floatingActionButton = findViewById(R.id.fab_cart);

        if (sharedPreferences.getString("login", null) == null)
        {
            Intent intent = new Intent(LocalActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.foreground));

            }

            if (sharedPreferences.getString("login", null) ==null)
            {
                floatingActionButton.hide();
                ///Do Nothing
            }
            else {
                floatingActionButton.show();

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LocalActivity.this, CartActivity.class);
                        startActivity(intent);
                    }
                });

                try {
                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("cid", null));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            String cid = sharedPreferences.getString("cid", null);

            try {
                JSONObject jsonObject = new JSONObject(cid);
                check = jsonObject.getString("customerId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            btnUnion = findViewById(R.id.local_unon);
            btnUpazilla = findViewById(R.id.local_upazilla);
            btnDistrict = findViewById(R.id.local_district);
            listView = findViewById(R.id.local_list);

            context = getApplicationContext();

            params = new RequestParams();
            params.put("action", "getLocalProducts");
            params.put("cid", check);
            
            client = new AsyncHttpClient();
            
            client.get(MYURL, params, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);


                    jsonArray = response;
                    sellerProductAdapter = new SellerProductAdapter(context, R.layout.seller_product_row);
                    listView.setAdapter(sellerProductAdapter);


                    int count = 0;
                    String name;
                    String price;
                    String sellerName;
                    String image;
                    String len;


                    btnUnion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            sellerProductAdapter.remove();

                            select = 0;
                            int ck = 0;
                            String name;
                            String price;
                            String sellerName;
                            String image;
                            String len;
                            byte[] imageByte;
                            try {
                                JSONArray jarray = jsonArray.getJSONArray(select);

                                while (ck <jarray.length()) {

                                    name = jarray.getJSONObject(ck).getString("productName");
                                    price = jarray.getJSONObject(ck).getString("productPrice");
                                    image = jarray.getJSONObject(ck).getString("productImageName");
                                    sellerName = jarray.getJSONObject(ck).getString("sellerId");

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


                                    SellerProduct sellerProduct = new SellerProduct();
                                    sellerProduct.setImage(bmp);
                                    sellerProduct.setName(name);
                                    sellerProduct.setPrice(price);
                                    sellerProduct.setSellerName(sellerName);


                                    sellerProductAdapter.add(sellerProduct);

                                    ck++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                    btnUpazilla.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sellerProductAdapter.remove();

                            select = 1;
                            int ck = 0;
                            String name;
                            String price;
                            String sellerName;
                            String image;
                            String len;
                            byte[] imageByte;
                            try {
                                JSONArray jarray = jsonArray.getJSONArray(select);

                                while (ck <jarray.length()) {

                                    name = jarray.getJSONObject(ck).getString("productName");
                                    price = jarray.getJSONObject(ck).getString("productPrice");
                                    image = jarray.getJSONObject(ck).getString("productImageName");
                                    sellerName = jarray.getJSONObject(ck).getString("sellerId");

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

                                    SellerProduct sellerProduct = new SellerProduct();
                                    sellerProduct.setImage(bmp);
                                    sellerProduct.setName(name);
                                    sellerProduct.setPrice(price);
                                    sellerProduct.setSellerName(sellerName);


                                    sellerProductAdapter.add(sellerProduct);

                                    ck++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btnDistrict.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sellerProductAdapter.remove();

                            select = 2;
                            int ck = 0;
                            String name;
                            String price;
                            String sellerName;
                            String image;
                            String len;
                            byte[] imageByte;
                            try {
                                JSONArray jarray = jsonArray.getJSONArray(select);

                                while (ck <jarray.length()) {

                                    name = jarray.getJSONObject(ck).getString("productName");
                                    price = jarray.getJSONObject(ck).getString("productPrice");
                                    image = jarray.getJSONObject(ck).getString("productImageName");
                                    sellerName = jarray.getJSONObject(ck).getString("sellerId");


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


                                    SellerProduct sellerProduct = new SellerProduct();
                                    sellerProduct.setImage(bmp);
                                    sellerProduct.setName(name);
                                    sellerProduct.setPrice(price);
                                    sellerProduct.setSellerName(sellerName);


                                    sellerProductAdapter.add(sellerProduct);

                                    ck++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent = new Intent();
                            intent.setClass(LocalActivity.this, ViewSellerProductActivity.class);
                            try {
                                String productDetails = jsonArray.getJSONArray(select).getJSONObject(position).toString();
                                intent.putExtra("product", productDetails);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LocalActivity.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                            // Or / And

                        }
                    });


                }





                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(LocalActivity.this, "Failed to Load", Toast.LENGTH_SHORT).show();
                }
            });



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
                menuItem1.setTitle("Logout");
            }





            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent1;
                    if (menuItem.getTitle().equals("আমাদের বাজার")) {
                        intent1 = new Intent(LocalActivity.this, MainActivity.class);
                        startActivity(intent1);
                    }
                    else if (menuItem.getTitle().equals("সব দেখুন")){
                        intent1 = new Intent(LocalActivity.this, BrowseAllActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                        intent1 = new Intent(LocalActivity.this, SellerActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                        intent1 = new Intent(LocalActivity.this, LocalActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                        intent1 = new Intent(LocalActivity.this, SearchActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("প্রফাইল")) {
                        intent1 = new Intent(LocalActivity.this, ProfileActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                        intent1 = new Intent(LocalActivity.this, SellerLoginActivity.class);
                        startActivity(intent1);
                    }

                    else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                        intent1 = new Intent(LocalActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                        editor.clear();
                        editor.commit();
                        intent1 = new Intent(LocalActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                        intent1 = new Intent(LocalActivity.this, ContactUsActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                        intent1 = new Intent(LocalActivity.this, AboutUsActivity.class);
                        startActivity(intent1);
                    }
                    return true;
                }
            });
        }
    }
}
