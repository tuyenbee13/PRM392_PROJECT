package com.example.electronics_store.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartUtils {

    private static final String CART_PREF = "cart";
    private static final String CART_ITEMS_KEY = "cart_items";

    // ✅ Thêm sản phẩm vào giỏ hàng (Kiểm tra trùng ID để tăng số lượng)
    public static void addToCart(Context context, ProductResponse product) {
        List<ProductResponse> cartList = getCartList(context);
        boolean productExists = false;

        for (ProductResponse item : cartList) {
            if (item.getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            product.setQuantity(1);
            cartList.add(product);
        }

        saveCartList(context, cartList);
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // ✅ Lấy danh sách giỏ hàng từ SharedPreferences
    public static List<ProductResponse> getCartList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREF, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);

        Type type = new TypeToken<List<ProductResponse>>() {}.getType();
        return json != null ? new Gson().fromJson(json, type) : new ArrayList<>();
    }

    // ✅ Lưu danh sách giỏ hàng vào SharedPreferences
    public static void saveCartList(Context context, List<ProductResponse> cartList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = new Gson().toJson(cartList);
        editor.putString(CART_ITEMS_KEY, json);
        editor.apply();
    }

    // ✅ Giảm số lượng hoặc xóa sản phẩm nếu số lượng về 0
    public static void updateCart(Context context, int productId, int change) {
        List<ProductResponse> cartList = getCartList(context);
        ProductResponse productToRemove = null;

        for (ProductResponse product : cartList) {
            if (product.getId() == productId) {
                int newQuantity = product.getQuantity() + change;

                if (newQuantity > 0) {
                    product.setQuantity(newQuantity);
                } else {
                    productToRemove = product;
                }
                break;
            }
        }

        if (productToRemove != null) {
            cartList.remove(productToRemove);
            Toast.makeText(context, "Sản phẩm đã bị xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
        }

        saveCartList(context, cartList);
    }

    // ✅ Xóa toàn bộ giỏ hàng
    public static void clearCart(Context context) {
        saveCartList(context, new ArrayList<>());
        Toast.makeText(context, "Bạn đã thanh toán thành công!!!", Toast.LENGTH_SHORT).show();
    }
}
