package com.kachabazars.ecommerce;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.CurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class CartOrderActivity extends AppCompatActivity implements TransactionResponseListener{


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    String check;
    String tranId;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TextView name;
    EditText coc, expectedDate, phone, quantity, village, street, zip;
    Spinner spinnerDivision, spinnerDistrict, spinnerUpazilla, spinnerUnion;
    ImageView imageView;
    Button btnCheckout;
    RequestParams params;
    AsyncHttpClient client;
    ArrayList<String> divisionList = new ArrayList<>();
    ArrayList<String> districtList = new ArrayList<>();
    ArrayList<String> upazillaList = new ArrayList<>();
    ArrayList<String> unionList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    double total;

    String expectedDeliveryDate, pid, cid;




    //  DivisionModel divisionModel = new DivisionModel();
    //  ArrayList<DivisionModel> divisionModels = new ArrayList<>();

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "rest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_order);


        Intent in = getIntent();
        total = in.getExtras().getDouble("total");

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        zip = findViewById(R.id.confirm_zipcode);
        street = findViewById(R.id.confirm_Street);
        village = findViewById(R.id.confirm_village);
        quantity = findViewById(R.id.confirm_quantity);
        phone = findViewById(R.id.confirm_phone_number);
        coc = findViewById(R.id.confirm_care_of_contact);
        btnCheckout = findViewById(R.id.checkout_button);
        spinnerDivision = findViewById(R.id.division_spinner);
        spinnerDistrict = findViewById(R.id.district_spinner);
        spinnerUpazilla = findViewById(R.id.upazilla_spinner);
        spinnerUnion = findViewById(R.id.union_spinner);
        expectedDate = findViewById(R.id.confirm_expected_date);
        imageView = findViewById(R.id.confirm_image);
        name = findViewById(R.id.confirm_name);

        if (sharedPreferences.getString("login", null) == null)
        {
            Intent intent = new Intent(CartOrderActivity.this, LoginActivity.class);
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

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            navigationView.setItemIconTintList(null);


            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            menu = navigationView.getMenu();
            menuItem1 = menu.findItem(R.id.nav_login);
            if (sharedPreferences.getString("login", null) == null) {
                ///Do Nothing
            } else {
                menuItem1.setTitle("লগ আউট করুন");
            }


            try {
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("cid", null));
                cid = jsonObject.getString("customerId");
            } catch (JSONException e) {
                e.printStackTrace();
            }



            client = new AsyncHttpClient();
            params = new RequestParams();

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



                        if (jsonArrayDivision != null)
                        {
                            for (int i=0; i<jsonArrayDivision.length(); i++)
                            {

                                divisionList.add(jsonArrayDivision.getJSONObject(i).getString("divisionBanglaName"));

                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, R.id.division, divisionList);
                            spinnerDivision.setAdapter(adapter);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(CartOrderActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(CartOrderActivity.this, "Failed Districts", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }


                                ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_district_layout, R.id.district, districtList);
                                spinnerDistrict.setAdapter(adapter1);
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            Toast.makeText(CartOrderActivity.this, "Failed to looad", Toast.LENGTH_SHORT).show();
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

                    upazillaList.clear();
                    unionList.clear();

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

                    unionList.clear();
                    params = new RequestParams();
                    params.put("action", "unionbyupazilla");
                    params.put("divId", spinnerDivision.getSelectedItemId() + 1);
                    params.put("disId", spinnerDistrict.getSelectedItemId());
                    params.put("upaId", position);


                    client = new AsyncHttpClient();
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
                                        unionList.add(response.getJSONObject(i).getString("unionBanglaName"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                ArrayAdapter<String> adapter3;
                                adapter3 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_union_layout, R.id.union, unionList);
                                spinnerUnion.setAdapter(adapter3);
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(CartOrderActivity.this, "Failed union", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });






                expectedDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(CartOrderActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                dateSetListener, year, month, day);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });

                dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = year + "-" + month + "-" + dayOfMonth;
                        expectedDeliveryDate = date;
                        expectedDate.setText(date);

                    }
                };


                btnCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Context context = v.getContext();

                        String careOfContact, phonestring, quantitystring, villagestring, streetstring, zipstring;

                        careOfContact = coc.getText().toString();
                        phonestring = phone.getText().toString();
                        villagestring = village.getText().toString();
                        streetstring = street.getText().toString();
                        zipstring = zip.getText().toString();





                        if (careOfContact.isEmpty() || phonestring.isEmpty() || villagestring.isEmpty() || streetstring.isEmpty() || zipstring.isEmpty())
                        {
                            Toast.makeText(CartOrderActivity.this, "দয়া করে সব তথ্য দিন", Toast.LENGTH_LONG).show();
                        } else{
                            String MYURL1 = ipConfig.myURI + "restcart";
                            RequestParams params1 = new RequestParams();
                            AsyncHttpClient clients = new AsyncHttpClient();

                            cid = sharedPreferences.getString("id", null);

                            params1.put("action", "cartorder");
                            params1.put("coc", careOfContact);
                            params1.put("phone", phonestring);
                            params1.put("village", villagestring);
                            params1.put("street", streetstring);
                            params1.put("zip", zipstring);
                            params1.put("divId", spinnerDivision.getSelectedItemId() + 1);
                            params1.put("disId", spinnerDistrict.getSelectedItemId());
                            params1.put("upaId", spinnerUpazilla.getSelectedItemId());
                            params1.put("uniId", spinnerUnion.getSelectedItemId());
                            params1.put("cid", cid);
                            params1.put("date", expectedDeliveryDate);

                            clients.post(MYURL1,params1, new JsonHttpResponseHandler(){

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {


                                    try {
                                        tranId = response.getJSONObject(0).getString("tranId");



                                        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization("kachabazarslive",
                                                "5DF89D41AE36A11457", total, CurrencyType.BDT,
                                                tranId, "food", SdkType.LIVE);


                                        IntegrateSSLCommerz
                                                .getInstance(context).addSSLCommerzInitialization(sslCommerzInitialization).buildApiCall(CartOrderActivity.this);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    Toast.makeText(CartOrderActivity.this, "Failed to sent to rest cart order", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });



        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent1;
                if (menuItem.getTitle().equals("আমাদের বাজার")) {
                    intent1 = new Intent(CartOrderActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else if (menuItem.getTitle().equals("সব দেখুন")){
                    intent1 = new Intent(CartOrderActivity.this, BrowseAllActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                    intent1 = new Intent(CartOrderActivity.this, SellerActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                    intent1 = new Intent(CartOrderActivity.this, LocalActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                    intent1 = new Intent(CartOrderActivity.this, SearchActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("প্রফাইল")) {
                    intent1 = new Intent(CartOrderActivity.this, ProfileActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                    intent1 = new Intent(CartOrderActivity.this, SellerLoginActivity.class);
                    startActivity(intent1);
                }

                else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                    intent1 = new Intent(CartOrderActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                    editor.clear();
                    editor.commit();
                    intent1 = new Intent(CartOrderActivity.this, MainActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                    intent1 = new Intent(CartOrderActivity.this, ContactUsActivity.class);
                    startActivity(intent1);
                } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                    intent1 = new Intent(CartOrderActivity.this, AboutUsActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });



    }

    public void transactionSuccess(TransactionInfoModel transactionInfoModel) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("action", "cartorderdone");
        params.put("tranId", tranId);

        client.post(MYURL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Toast.makeText(CartOrderActivity.this, response.getJSONObject(0).getString("mes"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartOrderActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void transactionFail(String s) {
        Toast.makeText(this, "Failed to pay", Toast.LENGTH_SHORT).show();
    }

    public void merchantValidationError(String s) {
        Toast.makeText(this, "Failed to validate merchant", Toast.LENGTH_SHORT).show();
    }
}
