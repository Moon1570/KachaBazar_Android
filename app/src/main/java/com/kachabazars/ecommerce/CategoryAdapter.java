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

public class CategoryAdapter extends ArrayAdapter {

    IpConfig ipConfig = new IpConfig();

    ArrayList list = new ArrayList();
    Context context;

    public CategoryAdapter(@NonNull Context context, int resource) {
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
        CategoryAdapter.CategoryHolder categoryHolder;

        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.category_row_layout, parent, false);

            categoryHolder = new CategoryAdapter.CategoryHolder();

            categoryHolder.imageView = row.findViewById(R.id.catImage);
            categoryHolder.name = row.findViewById(R.id.catName);


            row.setTag(categoryHolder);
        }
        else {
            categoryHolder = (CategoryAdapter.CategoryHolder) row.getTag();
        }

        Category category = (Category) this.getItem(position);
        categoryHolder.name.setText(category.getCategoryName());
        categoryHolder.imageView.setImageBitmap(category.getCategoryImage());

    /*    Picasso.get()
                .load("https://i.ytimg.com/vi/28uUsJ72a1A/hqdefault.jpg")
                .placeholder(null)
                .resize(80, 120)
                .into(productHolder.imageView); */

        return row;
    }

    static class CategoryHolder
    {
        TextView name, desc;
        ImageView imageView;
    }
}
