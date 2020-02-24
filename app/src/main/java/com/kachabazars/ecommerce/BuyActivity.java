package com.kachabazars.ecommerce;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import cz.msebera.android.httpclient.Header;

public class BuyActivity extends AppCompatActivity implements TransactionResponseListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    String check;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TextView name, stock;
    EditText coc, expectedDate, phone, quantity, village, street, zip;
    Spinner spinnerDivision, spinnerDistrict, spinnerUpazilla, spinnerUnion;
    ImageView imageView;
    Button confirmButton, cashOnDelivery;
    RequestParams params;
    AsyncHttpClient client;
    ArrayList<String> divisionList = new ArrayList<>();
    ArrayList<String> districtList = new ArrayList<>();
    ArrayList<String> unionList = new ArrayList<>();
    ArrayList<String> upazillaList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    double price;
    String expectedDeliveryDate, pid, cid, transId;
    double st;



  //  DivisionModel divisionModel = new DivisionModel();
  //  ArrayList<DivisionModel> divisionModels = new ArrayList<>();

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "rest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        zip = findViewById(R.id.confirm_zipcode);
        street = findViewById(R.id.confirm_Street);
        village = findViewById(R.id.confirm_village);
        quantity = findViewById(R.id.confirm_quantity);
        phone = findViewById(R.id.confirm_phone_number);
        coc = findViewById(R.id.confirm_care_of_contact);
        confirmButton = findViewById(R.id.confirm_button);
        spinnerDivision = findViewById(R.id.division_spinner);
        spinnerDistrict = findViewById(R.id.district_spinner);
        spinnerUpazilla = findViewById(R.id.upazilla_spinner);
        spinnerUnion = findViewById(R.id.union_spinner);
        expectedDate = findViewById(R.id.confirm_expected_date);
        imageView = findViewById(R.id.confirm_image);
        name = findViewById(R.id.confirm_name);
        stock = findViewById(R.id.confirm_stock);
        cashOnDelivery = findViewById(R.id.cash_on_delivery);

        if (sharedPreferences.getString("login", null) == null)
        {
            Intent intent = new Intent(BuyActivity.this, LoginActivity.class);
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
            

            Intent intent = getIntent();
            pid = intent.getExtras().getString("pid");
            String product = intent.getExtras().getString("product");
            final String pp = intent.getExtras().getString("pp");

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
                    Toast.makeText(BuyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(BuyActivity.this, "Failed Districts", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }


                                ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_district_layout, R.id.district, districtList);
                                spinnerDistrict.setAdapter(adapter1);
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            Toast.makeText(BuyActivity.this, "Failed to looad", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BuyActivity.this, "Failed union", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            try {
                final JSONObject jsonObject = new JSONObject(product);


                name.setText(jsonObject.getString("productName"));
                pid = jsonObject.getString("productId");
                String image = jsonObject.getString("productImageName");
                stock.setText("স্টক : " +jsonObject.getString("stock"));
                st = Double.parseDouble(jsonObject.getString("stock"));

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





                expectedDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(BuyActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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


            } catch (JSONException e) {
                e.printStackTrace();
            }


            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    String careOfContact, phonestring, quantitystring, villagestring, streetstring, zipstring;

                    careOfContact = coc.getText().toString();
                    phonestring = phone.getText().toString();
                    quantitystring = quantity.getText().toString();
                    villagestring = village.getText().toString();
                    streetstring = street.getText().toString();
                    zipstring = zip.getText().toString();

                    




                    if (careOfContact.isEmpty() || phonestring.isEmpty() || quantitystring.isEmpty() || villagestring.isEmpty() || streetstring.isEmpty() || zipstring.isEmpty())
                    {
                        Toast.makeText(BuyActivity.this, "দয়া করে সব তথ্য দিন", Toast.LENGTH_LONG).show();
                    }
                    else if (Double.parseDouble(quantitystring)>st){
                        Toast.makeText(context, "দুঃখিত! পণ্যটি শেষ ।", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());



                        transId =  UUID.randomUUID().toString()+ "_" + currentDate + currentTime;
                        String MYURL1 = ipConfig.myURI + "restorder";
                        RequestParams params1;
                        AsyncHttpClient clients;

                        double total = Double.parseDouble(pp) * Double.parseDouble(quantitystring);

                        params1 = new RequestParams();
                        params1.put("action", "orderfrominventory");
                        params1.put("coc", careOfContact);
                        params1.put("transId", transId);
                        params1.put("phone", phonestring);
                        params1.put("quantity", quantitystring);
                        params1.put("village", villagestring);
                        params1.put("street", streetstring);
                        params1.put("zip", zipstring);
                        params1.put("division", spinnerDivision.getSelectedItemId()+1);
                        params1.put("district", spinnerDistrict.getSelectedItemId()+1);
                        params1.put("upazilla", spinnerUpazilla.getSelectedItemId()+1);
                        params1.put("cid", cid);
                        params1.put("pid", pid);
                        params1.put("date", expectedDeliveryDate);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
                        }




                        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization("kachabazarslive",
                                "5DF89D41AE36A11457", total, CurrencyType.BDT,
                                transId, "food", SdkType.LIVE);


                        IntegrateSSLCommerz
                                .getInstance(context).addSSLCommerzInitialization(sslCommerzInitialization).buildApiCall(BuyActivity.this);


                    }
                }
            });

            cashOnDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String careOfContact, phonestring, quantitystring, villagestring, streetstring, zipstring;

                    careOfContact = coc.getText().toString();
                    phonestring = phone.getText().toString();
                    quantitystring = quantity.getText().toString();
                    villagestring = village.getText().toString();
                    streetstring = street.getText().toString();
                    zipstring = zip.getText().toString();

                    String MYURL1 = ipConfig.myURI + "restorder";
                    RequestParams params1;
                    AsyncHttpClient clients;

                    double total = price * Double.parseDouble(quantitystring);
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());



                    transId =  UUID.randomUUID().toString()+ "_" + currentDate + currentTime;


                    params1 = new RequestParams();
                    params1.put("action", "orderfrominventoryCOD");
                    params1.put("coc", careOfContact);
                    params1.put("phone", phonestring);
                    params1.put("quantity", quantitystring);
                    params1.put("village", villagestring);
                    params1.put("street", streetstring);
                    params1.put("zip", zipstring);
                    params1.put("transId", transId);
                    params1.put("divId", spinnerDivision.getSelectedItemId() + 1);
                    params1.put("disId", spinnerDistrict.getSelectedItemId());
                    params1.put("upaId", spinnerUpazilla.getSelectedItemId());
                    params1.put("uniId", spinnerUnion.getSelectedItemId());
                    params1.put("cid", cid);
                    params1.put("pid", pid);
                    params1.put("date", expectedDeliveryDate);

                    clients = new AsyncHttpClient();



                    clients.post(MYURL1, params1, new JsonHttpResponseHandler()
                    {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Intent intent1 = new Intent(BuyActivity.this, MainActivity.class);
                            Toast.makeText(BuyActivity.this, "আপ্পনার অর্ডারটি গ্রহণ করা হয়েছে", Toast.LENGTH_SHORT).show();
                            startActivity(intent1);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(BuyActivity.this, "Failed "+ responseString, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });



            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Intent intent1;
                    if (menuItem.getTitle().equals("আমাদের বাজার")) {
                        intent1 = new Intent(BuyActivity.this, MainActivity.class);
                        startActivity(intent1);
                    }
                    else if (menuItem.getTitle().equals("সব দেখুন")){
                        intent1 = new Intent(BuyActivity.this, BrowseAllActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রেতাদের বাজার")) {
                        intent1 = new Intent(BuyActivity.this, SellerActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("স্থানীয় বাজার")) {
                        intent1 = new Intent(BuyActivity.this, LocalActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বাজার খুজুন")) {
                        intent1 = new Intent(BuyActivity.this, SearchActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("প্রফাইল")) {
                        intent1 = new Intent(BuyActivity.this, ProfileActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("বিক্রি করুন")){
                        intent1 = new Intent(BuyActivity.this, SellerLoginActivity.class);
                        startActivity(intent1);
                    }

                    else if (menuItem.getTitle().equals("লগ ইন করুন")) {
                        intent1 = new Intent(BuyActivity.this, LoginActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("লগ আউট করুন")) {
                        editor.clear();
                        editor.commit();
                        intent1 = new Intent(BuyActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সাথে যোগাযোগ")) {
                        intent1 = new Intent(BuyActivity.this, ContactUsActivity.class);
                        startActivity(intent1);
                    } else if (menuItem.getTitle().equals("আমাদের সম্পরকে জানুন")) {
                        intent1 = new Intent(BuyActivity.this, AboutUsActivity.class);
                        startActivity(intent1);
                    }
                    return true;
                }
            });
        }
    }

    public void transactionSuccess(TransactionInfoModel transactionInfoModel) {
        Toast.makeText(BuyActivity.this, "Success Message... ", Toast.LENGTH_SHORT).show();

        String careOfContact, phonestring, quantitystring, villagestring, streetstring, zipstring;

        careOfContact = coc.getText().toString();
        phonestring = phone.getText().toString();
        quantitystring = quantity.getText().toString();
        villagestring = village.getText().toString();
        streetstring = street.getText().toString();
        zipstring = zip.getText().toString();

        String MYURL1 = ipConfig.myURI + "restorder";
        RequestParams params1;
        AsyncHttpClient clients;

        double total = price * Double.parseDouble(quantitystring);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());



        transId =  UUID.randomUUID().toString()+ "_" + currentDate + currentTime;


        params1 = new RequestParams();
        params1.put("action", "orderfrominventory");
        params1.put("coc", careOfContact);
        params1.put("phone", phonestring);
        params1.put("quantity", quantitystring);
        params1.put("village", villagestring);
        params1.put("street", streetstring);
        params1.put("zip", zipstring);
        params1.put("transId", transId);
        params1.put("divId", spinnerDivision.getSelectedItemId() + 1);
        params1.put("disId", spinnerDistrict.getSelectedItemId());
        params1.put("upaId", spinnerUpazilla.getSelectedItemId());
        params1.put("uniId", spinnerUnion.getSelectedItemId());
        params1.put("cid", cid);
        params1.put("pid", pid);
        params1.put("date", expectedDeliveryDate);

        clients = new AsyncHttpClient();



        clients.post(MYURL1, params1, new JsonHttpResponseHandler()
                        {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                super.onSuccess(statusCode, headers, response);
                                Intent intent1 = new Intent(BuyActivity.this, MainActivity.class);
                                startActivity(intent1);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Toast.makeText(BuyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
