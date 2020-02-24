package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity {


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
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.category_list);
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
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
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
        Log.e("look here", "vdjsb");
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
                            intent.setClass(MainActivity.this, BrowseAllActivity.class);
                            intent.putExtra("position", position);
                            try {
                                jsonArray.getJSONObject(position).toString();
                                String categoryDetails = "absd";
                                intent.putExtra("catId", position+1);
                                intent.putExtra("category",categoryDetails);
                                Log.e("look here", position+"");

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
                Toast.makeText(MainActivity.this, "Something went wrong please try again later !", Toast.LENGTH_SHORT).show();
            }
        });

        /*

         params = new RequestParams();
        params.put("action", "getProducts");
        client = new AsyncHttpClient();

        client.get(MYURL, params, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    jsonArray = response;
                    productAdapter = new ProductAdapter(context, R.layout.row_layout);
                    listView.setAdapter(productAdapter);

                    int count = 0;
                    String name, price, govtPrice, image;


                    while (count<jsonArray.getJSONArray(0).length())
                    {
                        JSONObject JO = jsonArray.getJSONArray(0).getJSONObject(count);
                        name = JO.getString("productName");
                        price = JO.getString("productPrice");
                        govtPrice = JO.getString("governmentPrice");
                        image = JO.getString("productImageName");

                        Product product = new Product(name, price, govtPrice, image);

                        productAdapter.add(product);

                        count++;
                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, ViewProductActivity.class);
                            intent.putExtra("position", position);
                            try {
                                String productDetails = jsonArray.getJSONArray(0).getJSONObject(position).toString();
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
            }
        });


     */







        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(MainActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(MainActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(MainActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(MainActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(MainActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(MainActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });



    /*    try {
            jsonArray = new JSONArray(array);

            productAdapter = new ProductAdapter(this, R.layout.row_layout);
            listView.setAdapter(productAdapter);

       //     productString = jsonArray.getJSONArray(1).toString();

       //     jsonObject = new JSONObject(productString);

       //     jsonArray2 = new JSONArray(productString);



            final String cid = jsonArray.getJSONArray(0).getJSONObject(0).getString("customerId");

            for (int i=0; i<jsonArray.getJSONArray(1).length(); i++)
            {
                textView.setText(jsonArray.getJSONArray(0).getJSONObject(i).toString());
                Toast.makeText(this, jsonArray.getJSONArray(0).getJSONObject(i).toString(), Toast.LENGTH_SHORT).show();
            }











        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
      //  textView.setText(jsonArray.getJSONObject(0).toString());

*/



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
