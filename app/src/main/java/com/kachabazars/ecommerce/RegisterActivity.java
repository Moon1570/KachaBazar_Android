package com.kachabazars.ecommerce;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kachabazars.ecommerce.config.IpConfig;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MenuItem menuItem1;
    Menu menu;
    public static final String mypreference = "mypref";
    String check;
    int resultLoadImg = 1;

    DatePickerDialog.OnDateSetListener dateSetListener;

    EditText dob, phone, village, street, hnum, zip, fname, lname, pass;
    Spinner spinnerDivision, spinnerDistrict, spinnerUpazilla, spinnerUnion;
    ImageView imageView;
    Button confirmButton, imgConfirmButton;
    RequestParams params;
    AsyncHttpClient client;
    ArrayList<String> divisionList = new ArrayList<>();
    ArrayList<String> districtList = new ArrayList<>();
    ArrayList<String> upazillaList = new ArrayList<>();
    ArrayList<String> unionList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    double price;
    String encoded;
    String expectedDeliveryDate, pid, cid, transId;




    //  DivisionModel divisionModel = new DivisionModel();
    //  ArrayList<DivisionModel> divisionModels = new ArrayList<>();

    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI + "rest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        dob = findViewById(R.id.reg_dob);
        phone = findViewById(R.id.reg_phone);
        village = findViewById(R.id.reg_village);
        street = findViewById(R.id.reg_street);
        hnum = findViewById(R.id.reg_holding_number);
        zip = findViewById(R.id.reg_zipcode);
        fname = findViewById(R.id.reg_first_name);
        lname = findViewById(R.id.reg_last_name);
        pass = findViewById(R.id.reg_password);

        imageView = findViewById(R.id.reg_select_image);

        spinnerDivision = findViewById(R.id.reg_division);
        spinnerDistrict = findViewById(R.id.reg_district);
        spinnerUpazilla = findViewById(R.id.reg_upazilla);
        spinnerUnion = findViewById(R.id.reg_union);

        confirmButton = findViewById(R.id.btn_reg_reg);
        imgConfirmButton = findViewById(R.id.btn_reg_confirm_image);

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



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imgConfirmButton.setVisibility(View.VISIBLE);
                startActivityForResult(gallery, resultLoadImg);
            }
        });

        imgConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image= ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Toast.makeText(RegisterActivity.this, encoded, Toast.LENGTH_SHORT).show();

                imgConfirmButton.setVisibility(View.INVISIBLE);
            }
        });


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

                    String division = response.getJSONArray(0).toString();

                    jsonArrayDivision = new JSONArray(division);



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
                Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(RegisterActivity.this, "Failed Districts", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }


                            ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_district_layout, R.id.district, districtList);
                            spinnerDistrict.setAdapter(adapter1);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        Toast.makeText(RegisterActivity.this, "Failed to looad", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterActivity.this, "Failed union", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = new RequestParams();
                String MYURL1 = ipConfig.myURI + "restlocal";

                String namef, namel, phn, vill, st, hnumb, zipp, password;

                namef = fname.getText().toString();
                namel = lname.getText().toString();
                phn = phone.getText().toString();
                vill = village.getText().toString();
                st = street.getText().toString();
                hnumb = hnum.getText().toString();
                zipp = zip.getText().toString();
                password = pass.getText().toString();


                if (namef == null || namel == null || phn == null || vill == null || st == null || hnumb == null || zipp == null || password.isEmpty() || expectedDeliveryDate == null || encoded == null){
                    Toast.makeText(RegisterActivity.this, "দয়া করে সব তথ্য দিন", Toast.LENGTH_SHORT).show();
                } else {

                    params.put("action", "reg");

                    params.put("fname", namef);
                    params.put("lname", namel);
                    params.put("phone", phn);
                    params.put("dob", expectedDeliveryDate);
                    params.put("village", vill);
                    params.put("street", st);
                    params.put("hnum", hnumb);
                    params.put("zip", zipp);
                    params.put("pass", password);

                    params.put("img", encoded);

                    params.put("divId", spinnerDivision.getSelectedItemId() + 1);
                    params.put("disId", spinnerDistrict.getSelectedItemId());
                    params.put("upaId", spinnerUpazilla.getSelectedItemId());
                    params.put("uniId", spinnerUnion.getSelectedItemId());

                    client = new AsyncHttpClient();

                    client.post(MYURL1, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);

                            String cid = null;
                            try {
                                cid = response.getJSONObject(0).toString();
                                editor.putString("login", "login");
                                editor.putString("cid", cid);
                                editor.putString("id", response.getJSONObject(0).getString("customerId"));
                                editor.commit();
                                
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(RegisterActivity.this, "নেটওয়ার্ক প্রব্লেম। কিছুক্ষন পরে আবার চেষ্টা করুণ", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                dob.setText(date);

            }
        };

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == resultLoadImg && data != null)
        {

            Uri selectedImageUri = data.getData();

            //    imgUpload.setImageURI(selectedImageUri);
            Picasso.get().load(selectedImageUri).into(imageView);



        }
    }

}
