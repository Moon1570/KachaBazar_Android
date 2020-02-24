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

public class SellerProductAdapter extends ArrayAdapter
{
    IpConfig ipConfig = new IpConfig();
    ArrayList list = new ArrayList();
    Context context;

    public SellerProductAdapter(@NonNull Context context, int resource)
    {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable Object object) {
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

    public void remove() {
        list.clear();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row1;
        row1 = convertView;
        SellerProductHolder sellerProductHolder;

        if (row1 == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row1 = layoutInflater.inflate(R.layout.seller_product_row, parent, false);

            sellerProductHolder = new SellerProductAdapter.SellerProductHolder();

            sellerProductHolder.imageView = row1.findViewById(R.id.seller_product_image);
            sellerProductHolder.name = row1.findViewById(R.id.seller_product_name);
            sellerProductHolder.price = row1.findViewById(R.id.seller_product_price);
            sellerProductHolder.sellerName = row1.findViewById(R.id.seller_name);


            row1.setTag(sellerProductHolder);
        }
        else {
            sellerProductHolder = (SellerProductHolder) row1.getTag();
        }


        SellerProduct sellerProduct = (SellerProduct) this.getItem(position);
        sellerProductHolder.name.setText(sellerProduct.getName());
        sellerProductHolder.price.setText(sellerProduct.getPrice());
        sellerProductHolder.sellerName.setText(sellerProduct.getSellerName());

        sellerProductHolder.imageView.setImageBitmap(sellerProduct.getImage());

    /*    Picasso.get()
                .load("https://i.ytimg.com/vi/28uUsJ72a1A/hqdefault.jpg")
                .placeholder(null)
                .resize(80, 120)
                .into(productHolder.imageView); */

        return row1;
    }


    static class SellerProductHolder
    {
        TextView name, price, sellerName;
        ImageView imageView;
    }
}
