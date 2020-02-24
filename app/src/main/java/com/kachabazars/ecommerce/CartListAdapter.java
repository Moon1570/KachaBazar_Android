package com.kachabazars.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kachabazars.ecommerce.config.IpConfig;

import java.util.ArrayList;

public class CartListAdapter extends ArrayAdapter {

    IpConfig ipConfig = new IpConfig();

    ArrayList list = new ArrayList();
    Context context;

    public CartListAdapter(@NonNull Context context, int resource) {
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
        View row;
        row = convertView;
        CartListAdapter.CartHolder cartHolder;

        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.cart_row_layout, parent, false);

            cartHolder = new CartListAdapter.CartHolder();

            cartHolder.sl = row.findViewById(R.id.cart_sl_no);
            cartHolder.name = row.findViewById(R.id.cart_product_name);
            cartHolder.price = row.findViewById(R.id.cart_product_price_list);
            cartHolder.stotal = row.findViewById(R.id.cart_subtotal);

            row.setTag(cartHolder);
        }
        else {
            cartHolder = (CartListAdapter.CartHolder) row.getTag();
        }

        CartView cartView = (CartView) this.getItem(position);
        cartHolder.name.setText(cartView.getName());
        cartHolder.sl.setText(cartView.getSl());
        cartHolder.price.setText(cartView.getPrice());
        cartHolder.stotal.setText(cartView.getStotal());


    /*    Picasso.get()
                .load("https://i.ytimg.com/vi/28uUsJ72a1A/hqdefault.jpg")
                .placeholder(null)
                .resize(80, 120)
                .into(productHolder.imageView); */

        return row;
    }

    static class CartHolder
    {
        TextView sl, name, price, stotal;
    }

}
