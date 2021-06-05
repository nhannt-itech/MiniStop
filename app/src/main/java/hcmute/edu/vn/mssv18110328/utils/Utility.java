package hcmute.edu.vn.mssv18110328.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Currency;

public class Utility {
    public static final String CURRENT_ID = "current_id";
    public static final String CURRENT_NAME = "current_name";
    public static final String CURRENT_PRODUCT_ID = "current_product_id";
    public static final String CURRENT_CATEGORY_ID = "current_category_id";
    public static final String CURRENT_PRODUCT_NAME = "current_product_name";
    public static final String INCOMPLETE_ORDER_STATUS= "incomplete";
    public static final String COMPLETE_ORDER_STATUS = "complete";
    public static final String CANCEL_ORDER_STATUS = "cancel";


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }

    public static String FormatPrice(double price)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("VND"));
        String result = format.format(price);
        return result;
    }
}
