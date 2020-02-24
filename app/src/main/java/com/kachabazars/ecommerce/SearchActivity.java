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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    public static final String login = "logout";
    public static final String cid = "null";
    String check;

    JSONArray jsonArray;
    ListView listView;
    SellerProductAdapter sellerProductAdapter;
    Context context;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    RequestParams params;
    AsyncHttpClient client;
    ArrayList<String> divisionList = new ArrayList<>();
    ArrayList<String> districtList = new ArrayList<>();
    ArrayList<String> upazillaList = new ArrayList<>();

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "rest";

    Spinner spinnerDivision, spinnerDistrict, spinnerUpazilla;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        floatingActionButton = findViewById(R.id.fab_cart);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.foreground));

        }
        context = getApplicationContext();

        spinnerDivision = findViewById(R.id.search_division);
        spinnerDistrict = findViewById(R.id.search_district);
        spinnerUpazilla = findViewById(R.id.search_upazilla);
        listView = findViewById(R.id.search_listview);

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
                    Intent intent = new Intent(SearchActivity.this, CartActivity.class);
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



        params = new RequestParams();
        params.put("action", "divisions");

        client = new AsyncHttpClient();

        client.get(MYURL, params, new JsonHttpResponseHandler(){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    JSONArray jsonArrayDivision = null;
                    JSONArray jsonArrayDistrict = null;
                    JSONArray jsonArrayUpazilla = null;

                    String division = response.getJSONArray(0).toString();
                    String district = response.getJSONArray(1).toString();
                    String  upazilla = response.getJSONArray(2).toString();

                    jsonArrayDivision = new JSONArray(division);
                    jsonArrayDistrict = new JSONArray(district);
                    jsonArrayUpazilla = new JSONArray(upazilla);


                    //    String divisionId, divisionName, divisionBanglaName;

                    if (jsonArrayDivision != null)
                    {
                        for (int i=0; i<jsonArrayDivision.length(); i++)
                        {

                            divisionList.add(jsonArrayDivision.getJSONObject(i).getString("divisionBanglaName"));

                      /*      divisionId = jsonArrayDivision.getJSONObject(i).getString("divisionId");
                            divisionName = jsonArrayDivision.getJSONObject(i).getString("divisionName");
                            divisionBanglaName = jsonArrayDivision.getJSONObject(i).getString("divisionBanglaName");

                            divisionModel.setDivisionId(divisionId);
                            divisionModel.setDivisionName(divisionName);
                            divisionModel.setDivisionBanglaName(divisionBanglaName);

                            divisionModels.add(divisionModel);*/


                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, R.id.division, divisionList);
                        spinnerDivision.setAdapter(adapter);
                    }

                /*    if (jsonArrayDistrict != null)
                    {
                        for (int i=0; i<jsonArrayDistrict.length(); i++)
                        {
                            districtList.add(jsonArrayDistrict.getJSONObject(i).getString("districtName"));
                        }


                        ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_district_layout, R.id.district, districtList);
                        spinnerDistrict.setAdapter(adapter1);
                    }

                    if (jsonArrayUpazilla != null)
                    {
                        for (int i=0; i<jsonArrayUpazilla.length(); i++)
                        {
                            upazillaList.add(jsonArrayUpazilla.getJSONObject(i).getString("upazillaName"));
                        }


                        ArrayAdapter<String> adapter2;
                        adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_upazilla_layout, R.id.upazilla, upazillaList);
                        spinnerUpazilla.setAdapter(adapter2);
                    }

                    */


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(SearchActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


        spinnerDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params = new RequestParams();
                params.put("action", "disctrictbydivision");
                params.put("divId", position+1);

                client = new AsyncHttpClient();

                districtList.clear();
                client.get(MYURL, params, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);


                        if (response != null)
                        {
                            for (int i=0; i<response.length(); i++)
                            {
                                try {

                                    districtList.add(response.getJSONObject(i).getString("districtBanglaName"));
                                } catch (JSONException e) {
                                    Toast.makeText(SearchActivity.this, "Failed Districts", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }


                            ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_district_layout, R.id.district, districtList);
                            spinnerDistrict.setAdapter(adapter1);
                        }

                    }
                });




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                params = new RequestParams();
                params.put("action", "upazillabydistrict");
                params.put("disId", position);
                params.put("divId", spinnerDivision.getSelectedItemId() + 1);

                client = new AsyncHttpClient();

                upazillaList.clear();
                client.get(MYURL, params, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);


                        if (response != null)
                        {
                            for (int i=0; i<response.length(); i++)
                            {
                                try {
                                    upazillaList.add(response.getJSONObject(i).getString("upazillaBangaName"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            ArrayAdapter<String> adapter2;
                            adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_upazilla_layout, R.id.upazilla, upazillaList);
                            spinnerUpazilla.setAdapter(adapter2);
                        }

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerUpazilla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                params = new RequestParams();
                params.put("action", "getSellerProductByUpaId");
                params.put("divId", spinnerDivision.getSelectedItemId() + 1);
                params.put("disId", spinnerDistrict.getSelectedItemId());
                params.put("upaId", position);


                client = new AsyncHttpClient();
                client.get(MYURL, params, new JsonHttpResponseHandler()
                {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            jsonArray = response;
                            sellerProductAdapter = new SellerProductAdapter(context, R.layout.seller_product_row);

                            sellerProductAdapter.remove();

                            listView.setAdapter(sellerProductAdapter);


                            int count = 0;
                            String name, price, sellerName, image, len;
                            byte[] imageByte;

                            Toast.makeText(SearchActivity.this, "v " + response.getJSONObject(0).getString("productName") + "  " , Toast.LENGTH_SHORT).show();

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
                                    intent.setClass(SearchActivity.this, ViewSellerProductActivity.class);
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
                            Toast.makeText(SearchActivity.this, "কোন বাজার পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(SearchActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(SearchActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(SearchActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(SearchActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(SearchActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(SearchActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(SearchActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(SearchActivity.this, AboutUsActivity.class);
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
