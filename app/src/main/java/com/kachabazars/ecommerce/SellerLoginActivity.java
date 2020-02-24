package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import cz.msebera.android.httpclient.Header;

public class SellerLoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String mypreference = "mypref";

    String check;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    EditText loginPhone;
    EditText loginPass;
    Button btnLogin;
    String phone, pass;
    RequestParams params;
    AsyncHttpClient client;

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "restseller";
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.foreground));

        }

        if (sharedPreferences.getString("seller", null) == null)
        {
            loginPhone = findViewById(R.id.seller_login_phone);
            loginPass = findViewById(R.id.seller_login_password);
            btnLogin = findViewById(R.id.btn_seller_login);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phone = loginPhone.getText().toString();
                    pass = loginPass.getText().toString();

                    params = new RequestParams();
                    params.put("k1", phone);
                    params.put("k2", pass);
                    params.put("action", "sellerlogin");

                    client = new AsyncHttpClient();


                    client.post(MYURL, params, new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);

                            String sid = null;
                            try {
                                sid = response.getJSONArray(0).getJSONObject(0).toString();
                                editor.putString("seller", "login");
                                editor.putString("sid", sid);
                                editor.commit();
                                Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(SellerLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

        }
        else {
            Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
            startActivity(intent);
        }
    }
}
