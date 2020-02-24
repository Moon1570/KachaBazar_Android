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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kachabazars.ecommerce.config.IpConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SellerHomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    public static final String mypreference = "mypref";

    String check;

    Menu menu;
    TextView welcome;
    Button btnSellerLogout, btnSellerAddProduct;
    String productString;
    JSONArray jsonArray;
    JSONArray jsonArray2;
    JSONObject jsonObject;
    ProductAdapter productAdapter;
    ListView listView;
    RequestParams params;
    AsyncHttpClient client;
    SellerProductAdapter sellerProductAdapter;

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "restseller";
    Context context;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        context = getApplicationContext();

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        listView = findViewById(R.id.seller_home_list);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.foreground));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (sharedPreferences.getString("seller", null) == null) {
            Intent intent = new Intent(SellerHomeActivity.this, SellerLoginActivity.class);
            startActivity(intent);
        } else {

            welcome = findViewById(R.id.seller_welcome);
            btnSellerAddProduct = findViewById(R.id.btn_seller_addproduct);
            btnSellerLogout = findViewById(R.id.btn_seller_logout);

            imageView = findViewById(R.id.i1);


            btnSellerLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.remove("seller");
                    editor.remove("sid");
                    editor.commit();
                    Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });


            String sid = sharedPreferences.getString("sid", null);
            String sellerId = null;

            try {
                JSONObject jsonObject = new JSONObject(sid);
                welcome.setText("Welcome " + jsonObject.getString("sellerFirstName"));
                sellerId = jsonObject.getString("sellerId");

            } catch (JSONException e) {
                e.printStackTrace();

            }


            params = new RequestParams();
            params.put("action", "getSellerProductsById");
            params.put("sid", sellerId);
            client = new AsyncHttpClient();


            client.get(MYURL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);


                    jsonArray = response;
                    sellerProductAdapter = new SellerProductAdapter(context, R.layout.seller_product_row);
                    listView.setAdapter(sellerProductAdapter);

                    int ck = 0;
                    String name;
                    String price;
                    String sellerName;
                    String image;
                    String len;
                    byte[] imageByte;
                    try {
                        JSONArray jarray = response;


                        while (ck <jarray.length()) {

                            name = jarray.getJSONObject(ck).getString("productName");
                            price = jarray.getJSONObject(ck).getString("productPrice");
                            image = jarray.getJSONObject(ck).getString("productImageName");
                            sellerName = jarray.getJSONObject(ck).getString("sellerId");

                            imageByte = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
                            Bitmap bmp= BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);


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

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(SellerHomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                }
            });

            btnSellerAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SellerHomeActivity.this, AddSellerProductActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
