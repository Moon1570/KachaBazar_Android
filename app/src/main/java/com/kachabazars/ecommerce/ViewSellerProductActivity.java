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
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ViewSellerProductActivity extends AppCompatActivity {


    TextView name, price, desc, seller, govtPrice;
    ImageView imageView;
    Button button;
    String cid, product, pid, pp;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    IpConfig ipConfig = new IpConfig();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_seller_product);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        name = findViewById(R.id.view_seller_product_name);
        price = findViewById(R.id.view_seller_product_price);
        desc = findViewById(R.id.view_seller_product_description);
        imageView =findViewById(R.id.view_seller_product_image);
        seller = findViewById(R.id.view_seller_name);
        govtPrice = findViewById(R.id.view_seller_product_govt_price);

        button = findViewById(R.id.btn_buy_from_seller);

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
            seller.setText("বিক্রেতা : " + jsonObject.getString("sellerName"));
            govtPrice.setText("সরকারি মূল্য : " + jsonObject.getString("govtPrice") + " /=");
            price.setText("বিক্রেতার নির্ধারিত মূল্য : " + jsonObject.getString("productPrice") + " /=");

            pp = jsonObject.getString("productPrice");

            pid = jsonObject.getString("productId");

            String image = jsonObject.getString("productImageName");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ViewSellerProductActivity.this,BuySellerProductActivity.class);
                intent1.putExtra("pid", pid);
                intent1.putExtra("product", product);
                intent1.putExtra("pp", pp);
                startActivity(intent1);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(ViewSellerProductActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(ViewSellerProductActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(ViewSellerProductActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(ViewSellerProductActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });

    }
}
