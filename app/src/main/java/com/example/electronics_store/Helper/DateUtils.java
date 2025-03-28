package com.example.electronics_store.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date); // Trả về ngày đã định dạng
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date"; // Nếu lỗi, trả về chuỗi mặc định
        }
    }
}
