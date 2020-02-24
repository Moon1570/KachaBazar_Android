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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kachabazars.ecommerce.config.IpConfig;
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

public class ViewProductActivity extends AppCompatActivity {




    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    String check, pp;

    RequestParams params;
    AsyncHttpClient client;

    TextView name, price, desc, stock;
    ImageView imageView;
    Button button;
    String cid, product, pid;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    IpConfig ipConfig = new IpConfig();
    EditText cartQty;
    Button btnCart;
    double st;


    String MYURL = ipConfig.myURI + "restcart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        name = findViewById(R.id.view_product_name);
        price = findViewById(R.id.view_product_price);
        desc = findViewById(R.id.view_product_description);
        imageView =findViewById(R.id.view_product_image);
        button = findViewById(R.id.btn_buy_from_inventory);
        cartQty = findViewById(R.id.add_cart_qty);
        btnCart = findViewById(R.id.btn_add_to_cart);
        stock = findViewById(R.id.view_product_stock);

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


        final Intent intent = getIntent();
        product = intent.getExtras().getString("product");

        try {
            final JSONObject jsonObject = new JSONObject(product);
            name.setText("নাম : " +jsonObject.getString("productName"));
            desc.setText("বিবরণ : "+jsonObject.getString("productDescription"));
            price.setText("দাম : "+jsonObject.getString("productPrice")+" TK");
            st = Double.parseDouble(jsonObject.getString("stock"));
            stock.setText("স্টক : " + jsonObject.getString("stock"));

            pp = jsonObject.getString("productPrice");


            pid = jsonObject.getString("productId");
            final String image = jsonObject.getString("productImageName");
            byte[] imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
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
            imageView.setImageBitmap(bmp);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ViewProductActivity.this,BuyActivity.class);
                    intent1.putExtra("pid", pid);
                    intent1.putExtra("product", product);
                    intent1.putExtra("pp", pp);
                    startActivity(intent1);
                }
            });

            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qty = cartQty.getText().toString();
                    Double qt = Double.parseDouble(qty);

                    if (sharedPreferences.getString("login", null) == null)
                    {
                        Intent intent = new Intent(ViewProductActivity.this, LoginActivity.class);
                        Toast.makeText(ViewProductActivity.this, "কার্টে রাখতে আগে লগইন করুণ", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    if (qty.isEmpty()){
                        Toast.makeText(ViewProductActivity.this, "কার্ট এ কতটি রাখবেন ?", Toast.LENGTH_SHORT).show();
                    }else if (qt > st){
                        Toast.makeText(ViewProductActivity.this, "পণ্যটি শেষ হয়ে গিয়েছে", Toast.LENGTH_SHORT).show();
                    }
                    else if (sharedPreferences.getString("login", null) == null)
                    {
                        Intent intent = new Intent(ViewProductActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else {
                        String id = sharedPreferences.getString("id", null);

                        client = new AsyncHttpClient();
                        params  = new RequestParams();

                        params.put("cid", id);
                        params.put("pid", pid);
                        params.put("qty", qty);
                        params.put("action", "cartadd");

                        client.post(MYURL, params, new JsonHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                super.onSuccess(statusCode, headers, response);
                                Toast.makeText(ViewProductActivity.this, "কার্টে রাখা হয়েছে", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(ViewProductActivity.this, MainActivity.class);
                                startActivity(intent1);

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                            }
                        });
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(ViewProductActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(ViewProductActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(ViewProductActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(ViewProductActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(ViewProductActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(ViewProductActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(ViewProductActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(ViewProductActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(ViewProductActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(ViewProductActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(ViewProductActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });
    }
}
