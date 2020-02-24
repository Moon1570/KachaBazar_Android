package com.kachabazars.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kachabazars.ecommerce.config.IpConfig;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter {

    IpConfig ipConfig = new IpConfig();

    ArrayList list = new ArrayList();
    Context context;

    public ProductAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }


    public void add(Product object) {
        super.add(object);
        list.add(object);
}

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        row = convertView;
        ProductHolder productHolder;

        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false);

            productHolder = new ProductHolder();

            productHolder.imageView = row.findViewById(R.id.image);
            productHolder.name = row.findViewById(R.id.product_name);
            productHolder.price = row.findViewById(R.id.product_price);
            productHolder.govtPrice = row.findViewById(R.id.product_govt_price);


            row.setTag(productHolder);
        }
        else {
            productHolder = (ProductHolder) row.getTag();
        }

        Product product = (Product) this.getItem(position);
        productHolder.name.setText(product.getName()+"");
        productHolder.price.setText(product.getPrice()+"");
        productHolder.govtPrice.setText(product.getGovtPrice()+"");


        productHolder.imageView.setImageBitmap(product.getImage());

    /*    Picasso.get()
                .load("https://i.ytimg.com/vi/28uUsJ72a1A/hqdefault.jpg")
                .placeholder(null)
                .resize(80, 120)
                .into(productHolder.imageView); */

        return row;
    }

    static class ProductHolder
    {
        TextView name, price, govtPrice;
        ImageView imageView;
    }
}
