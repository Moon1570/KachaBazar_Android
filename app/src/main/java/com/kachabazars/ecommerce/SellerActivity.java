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
import androidx.core.view.GravityCompat;
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

import cz.msebera.android.httpclient.Header;

public class SellerActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    public static final String mypreference = "mypref";

    String check;

    Menu menu;

    FloatingActionButton floatingActionButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    String productString;
    JSONArray jsonArray;
    JSONArray jsonArray2;
    JSONObject jsonObject;
    SellerProductAdapter sellerProductAdapter;
    ListView listView;
    RequestParams params;
    AsyncHttpClient client;
    CategoryAdapter categoryAdapter;

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "rest";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);


        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.seller_product_list);
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
        if (sharedPreferences.getString("login", null) == null) {
            floatingActionButton.hide();
            ///Do Nothing
        } else {
            floatingActionButton.show();
            menuItem1.setTitle("লগ আউট করুন");

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SellerActivity.this, CartActivity.class);
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

        context = getApplicationContext();



        params = new RequestParams();
        params.put("action", "getCategories");
        client = new AsyncHttpClient();


        client.get(MYURL, params, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    jsonArray = response;
                    categoryAdapter = new CategoryAdapter(context, R.layout.category_row_layout);
                    listView.setAdapter(categoryAdapter);

                    int count = 0;
                    String name, description, govtPrice, image, id;
                    byte[] imageByte;


                    while (count<jsonArray.length())
                    {
                        JSONObject JO = jsonArray.getJSONObject(count);
                        id = JO.getString("categoryId");
                        name = JO.getString("categoryName");
                        description = JO.getString("categoryDesc");
                        image = JO.getString("categoryImage");
                        //imageByte = Base64.getDecoder().decode(image);
                        imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
                        Bitmap bmp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                        Category category = new Category(name, description, bmp);
                        categoryAdapter.add(category);

                        count++;
                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.setClass(SellerActivity.this, BrowseSellerProductActivity.class);
                            intent.putExtra("position", position);
                            try {
                                jsonArray.getJSONObject(position).toString();
                                String categoryDetails = "abcd";
                                intent.putExtra("category",categoryDetails);
                                intent.putExtra("catId", position+1);
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
                Toast.makeText(SellerActivity.this, "Something went wrong please try again later !", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        context = getApplicationContext();

        params = new RequestParams();
        params.put("action", "getSellerProducts");
        client = new AsyncHttpClient();


        client.get(MYURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    jsonArray = response;
                    sellerProductAdapter = new SellerProductAdapter(context, R.layout.seller_product_row);
                    listView.setAdapter(sellerProductAdapter);


                    int count = 0;
                    String name, price, sellerName, image, len;
                    byte[] imageByte;

                    while (count<jsonArray.length()) {

                        name = jsonArray.getJSONObject(count).getString("productName");
                        price = jsonArray.getJSONObject(count).getString("productPrice");
                        image = jsonArray.getJSONObject(count).getString("productImageName");
                        sellerName = jsonArray.getJSONObject(count).getString("sellerName");

                        imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
                        Bitmap bmp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);

                        SellerProduct sellerProduct = new SellerProduct();
                        sellerProduct.setImage(bmp);
                        sellerProduct.setName(name);
                        sellerProduct.setPrice(price);
                        sellerProduct.setSellerName(sellerName);


                        sellerProductAdapter.add(sellerProduct);

                        count++;
                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent = new Intent();
                            intent.setClass(SellerActivity.this, ViewSellerProductActivity.class);
                            intent.putExtra("position", position);
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
                Toast.makeText(SellerActivity.this, "Failed to load Sellers Market", Toast.LENGTH_SHORT).show();

            }
        });

*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(SellerActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(SellerActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(SellerActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(SellerActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(SellerActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(SellerActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(SellerActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(SellerActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(SellerActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(SellerActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(SellerActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
