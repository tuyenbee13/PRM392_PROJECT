package com.example.electronics_store.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteManager {
    private static final String PREF_NAME = "FAVORITES";
    private static final String KEY_FAVORITES = "favorite_products";

    // ✅ Thêm sản phẩm vào danh sách yêu thích (tránh trùng)
    public static void addFavorite(Context context, ProductResponse product) {
        List<ProductResponse> favorites = getFavorites(context);
        Set<Integer> favoriteIds = new HashSet<>();

        for (ProductResponse p : favorites) {
            favoriteIds.add(p.getId());
        }

        if (favoriteIds.contains(product.getId())) {
            Toast.makeText(context, "Sản phẩm đã có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }

        favorites.add(product);
        saveFavorites(context, favorites);
        Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
    }

    // ✅ Xóa sản phẩm khỏi danh sách yêu thích
    public static void removeFavorite(Context context, int productId) {
        List<ProductResponse> favorites = getFavorites(context);
        boolean removed = favorites.removeIf(p -> p.getId() == productId);

        if (removed) {
            saveFavorites(context, favorites);
            Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Sản phẩm không có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Kiểm tra sản phẩm có trong danh sách yêu thích hay không
    public static boolean isFavorite(Context context, int productId) {
        List<ProductResponse> favorites = getFavorites(context);
        for (ProductResponse product : favorites) {
            if (product.getId() == productId) {
                return true;
            }
        }
        return false;
    }

    // ✅ Lấy danh sách sản phẩm yêu thích
    public static List<ProductResponse> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITES, null);

        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<ProductResponse>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    // ✅ Lưu danh sách yêu thích vào SharedPreferences
    public static void saveFavorites(Context context, List<ProductResponse> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (list.isEmpty()) {
            editor.remove(KEY_FAVORITES);
        } else {
            String json = new Gson().toJson(list);
            editor.putString(KEY_FAVORITES, json);
        }

        editor.apply();
    }
}
