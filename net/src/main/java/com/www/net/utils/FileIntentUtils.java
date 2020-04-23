package com.www.net.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Android 7.0+ 用于打开相应文件
 */
public class FileIntentUtils {

    public static void openFileEx(String filePath, String fileType, Context context) {
        try {
            Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            Uri data = Uri.fromFile(new File(filePath));
            intent.setDataAndType(data, getMap(fileType));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("FileIntentUtils：","找不到可以打开该文件的APP ");
        }

    }

    public static void openFileEx(Uri uri, String fileType, Context context) {
        try {
            Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            intent.setDataAndType(uri, fileType);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("FileIntentUtils：","找不到可以打开该文件的APP ");
        }

    }

    /**
     * 根据uri删除文件
     *
     * @param context
     * @param uri
     */
    public static void deleteUri(Context context, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            // content://开头的Uri
            context.getContentResolver().delete(uri, null, null);
        } else {
            File file = new File(FileUtils.getRealFilePath(context, uri));
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * 是否存在某文件
     *
     * @return
     */
    public static boolean isFile(Context context, Uri uri) {
        File file = new File(FileUtils.getRealFilePath(context, uri));
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }


    /**
     * 获取常见文件类型
     * @param key
     * @return
     */
    public static String getMap(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("rar", "application/x-rar-compressed");
        map.put("jpg", "image/jpeg");
        map.put("png", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("zip", "application/zip");
        map.put("pdf", "application/pdf");
        map.put("doc", "application/msword");
        map.put("docx", "application/msword");
        map.put("wps", "application/msword");
        map.put("xls", "application/vnd.ms-excel");
        map.put("et", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.ms-excel");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("html", "text/html");
        map.put("htm", "text/html");
        map.put("txt", "text/html");
        map.put("mp3", "audio/mpeg");
        map.put("mp4", "video/mp4");
        map.put("3gp", "video/3gpp");
        map.put("wav", "audio/x-wav");
        map.put("avi", "video/x-msvideo");
        map.put("flv", "flv-application/octet-stream");
        map.put("", "*/*");

        return map.get(key.toLowerCase());
    }
}