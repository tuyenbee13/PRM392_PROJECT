package com.example.electronics_store.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ProductResponse;
import java.util.List;

public class CartItemAdapter extends ArrayAdapter<ProductResponse> {
    private final Context context;
    private final List<ProductResponse> cartItems;

    public CartItemAdapter(Context context, List<ProductResponse> cartItems) {
        super(context, 0, cartItems);
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        }

        ProductResponse product = cartItems.get(position);

        TextView itemName = convertView.findViewById(R.id.item_name);
        TextView itemPrice = convertView.findViewById(R.id.item_price);
        TextView itemQuantity = convertView.findViewById(R.id.item_quantity);

        itemName.setText(product.getName());
        itemPrice.setText(String.format("%.0fđ", product.getPrice()));
        itemQuantity.setText("Số lượng: " + product.getQuantity());

        return convertView;
    }
}

