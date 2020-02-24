package com.kachabazars.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kachabazars.ecommerce.config.IpConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AddSellerProductActivity extends AppCompatActivity {

    TextView govtPrice;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String mypreference = "mypref";
    public static final String login = "logout";
    String check;
    ImageView imgUpload;
    Button btnConfirmImage, btnAddProduct;
    EditText productName, productQuantity, productDesc, productPrice;
    int resultLoadImg = 1;
    String encoded;
    RequestParams params;
    AsyncHttpClient client;
    IpConfig ipConfig = new IpConfig();
    String MYURL = ipConfig.myURI+"restsellerproduct";
    Context context;
    Spinner unitSpinner, categorySpinner, subcategorySpinner;
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> subcategoryList = new ArrayList<>();
    ArrayList<String> unitList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seller_product);

        context = getApplicationContext();

        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (sharedPreferences.getString("seller", null) == null) {
            Intent intent = new Intent(AddSellerProductActivity.this, SellerLoginActivity.class);
            startActivity(intent);
        }


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.foreground));
        }

        unitSpinner = findViewById(R.id.add_seller_product_spinner_unit);
        categorySpinner = findViewById(R.id.add_seller_product_category_spinner);
        subcategorySpinner = findViewById(R.id.add_seller_product_subcategory_spinner);
        
        btnConfirmImage = findViewById(R.id.btn_add_seller_product_confirm_image);
        btnAddProduct = findViewById(R.id.btn_add_seller_product);
        imgUpload = findViewById(R.id.add_seller_product_image);
        productName = findViewById(R.id.add_seller_product_name);
        productQuantity = findViewById(R.id.add_seller_product_quantity);
        productDesc = findViewById(R.id.add_seller_product_desc);
        productPrice = findViewById(R.id.add_seller_product_price);
        govtPrice = findViewById(R.id.add_seller_product_govt_price);


        params = new RequestParams();
        params.put("action", "getselleraddspinner");
        client = new AsyncHttpClient();


        client.get(MYURL, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray jsonArrayCategory = null;
                    JSONArray jsonArraySubcategory = null;
                    JSONArray jsonArrayUnit = null;

                    String category = response.getJSONArray(0).toString();
                    String subcategory = response.getJSONArray(1).toString();
                    String unit = response.getJSONArray(2).toString();

                    jsonArrayCategory = new JSONArray(category);
                    jsonArraySubcategory = new JSONArray(subcategory);
                    jsonArrayUnit = new JSONArray(unit);

                    if (jsonArrayCategory != null)
                    {
                        for (int i=0; i<jsonArrayCategory.length(); i++)
                        {
                            categoryList.add(jsonArrayCategory.getJSONObject(i).getString("categoryName"));
                        }


                        ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_category, R.id.seller_product_category, categoryList);
                        categorySpinner.setAdapter(adapter1);
                    }

                    if (jsonArraySubcategory != null)
                    {
                        for (int i=0; i<jsonArraySubcategory.length(); i++)
                        {
                            subcategoryList.add(jsonArraySubcategory.getJSONObject(i).getString("subcategoryName"));
                        }


                        ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_subcategory, R.id.seller_product_subcategory, subcategoryList);
                        subcategorySpinner.setAdapter(adapter1);
                    }

                    if (jsonArrayUnit != null)
                    {
                        for (int i=0; i<jsonArrayUnit.length(); i++)
                        {
                            unitList.add(jsonArrayUnit.getJSONObject(i).getString("unit"));
                        }


                        ArrayAdapter<String> adapter1 =  new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_unit, R.id.seller_product_unit, unitList);
                        unitSpinner.setAdapter(adapter1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });





        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, resultLoadImg);

            }
        });

        btnConfirmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap image= ((BitmapDrawable)imgUpload.getDrawable()).getBitmap();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                btnConfirmImage.setVisibility(View.INVISIBLE);
            }
        });
        
        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                client = new AsyncHttpClient();
                params = new RequestParams();
                params.put("action", "getGovtPrice");
                params.put("subId", position+1);

                client.get(MYURL, params, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            govtPrice.setText("সরকারি মূল্য : " + response.getJSONObject(0).getString("govtPrice") + " TK");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(AddSellerProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name, quantity, price, description;

                name = productName.getText().toString();
                quantity = productQuantity.getText().toString();
                price = productPrice.getText().toString();
                description = productDesc.getText().toString();

                if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || description.isEmpty())
                {
                    Toast.makeText(context, "দয়া করে সব তথ্য দিন", Toast.LENGTH_LONG).show();
                }
                else {
                    params = new RequestParams();
                    params.put("action", "addsellerproduct");
                    params.put("image", encoded);
                    params.put("name", name);
                    params.put("quantity", quantity);
                    params.put("price", price);
                    params.put("description", description);
                    params.put("catId", categorySpinner.getSelectedItemId()+1);
                    params.put("subcatId", subcategorySpinner.getSelectedItemId()+1);
                    params.put("unitId", unitSpinner.getSelectedItemId()+1);



                    String seller = sharedPreferences.getString("sid", null);
                    try {
                        JSONObject jsonObject = new JSONObject(seller);
                        params.put("sid", jsonObject.getString("sellerId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    client = new AsyncHttpClient();

                    client.post(MYURL, params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddSellerProductActivity.this, SellerHomeActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                }



            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == resultLoadImg && data != null)
        {

            Uri selectedImageUri = data.getData();

       //    imgUpload.setImageURI(selectedImageUri);
            Picasso.get().load(selectedImageUri).into(imgUpload);



        }
    }


}
