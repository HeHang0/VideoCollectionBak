package com.exer.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.vov.vitamio.utils.Base64;

public class Tools {
    @SuppressWarnings("unused")
    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null)
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getShortTime(){
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static int getBatterLevel(Context context){
        BatteryManager batteryManager=(BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager == null) return 100;
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY );
    }



    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        assert imm != null;
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(url.openStream(), null, options);
            // 获取这个图片的宽和高，注意此处的bitmap为null
           // bitmap = BitmapFactory.decodeFile(imagePath, options);
            options.inJustDecodeBounds = false; // 设为 false
            // 计算缩放比
            int h = options.outHeight;
            int w = options.outWidth;
            int beWidth = w / width;
            int beHeight = h / height;
            int be = 1;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if (be <= 0) {
                be = 1;
            }
            options.inSampleSize = be;
            // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
            bitmap = BitmapFactory.decodeStream(url.openStream(), null, options);
            // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // AES加密
    public static String aesEncrypt(String str, String key)  {
        String iv = "0102030405060708";
        if (str == null || key == null) return "";
        try {
            //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"), new IvParameterSpec(iv.getBytes("utf-8")));
            //byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"), ivParameterSpec);
            String base64Str = Base64.encodeToString(cipher.doFinal(str.getBytes("utf-8")), Base64.DEFAULT);
            return base64Str.replaceAll("\n", "").replaceAll("\r", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStrWithRegular(String pattern, String str){
        Matcher m = Pattern.compile(pattern).matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static String md5Encoder(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String newstr=Base64.encodeToString(md5.digest(str.getBytes("utf-8")), Base64.DEFAULT);
            return newstr;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
