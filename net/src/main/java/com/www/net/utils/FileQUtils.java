package com.www.net.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Created by Petterp
 * on 2019-12-27
 * <p>
 * 以下方法适用于Android10
 * <p>
 * Android10 文件存储机制为沙盒存储，每个App都只能访问自身目录下的文件(私有文件)和公共存储文件
 * <p>
 * 什么是私有文件？
 * 包名底下的文件，如 com.business.toos/xxx，读写私有文件无需任何权限
 * <p>
 * 什么是公有文件？
 * 如DCIM,PICTURES,MUSIC,Movies...，访问共有文件还是需要申请权限
 * <p>
 * 在AndroidQ中，文件的操作使用 ContentResolver 进行操作
 * 获取及创建私有文件使用 context.getExternalFilesDir( xxx )
 * 需要注意，沙盒文件会在卸载App之后被删除
 * <p>
 * 公共目录下文件操作
 * 通过MediaStore
 * <p>
 * 官网链接 https://developer.android.google.cn/training/data-storage/files/media
 */
public class FileQUtils {

    private static final String TAG = "FileQUtils";

    /**
     * 用于Android10保存image
     *
     * @param context
     * @param saveFileName 文件名
     * @param saveDirName  子文件路径，如果不存在则新建
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri saveImageWithAndroidQ(Context context,
                                            String saveFileName,
                                            String saveDirName) {
        Uri uri = null;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + saveDirName);
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        //这里为了避免用户如果删除了DCIM文件夹，此时让系统帮我们去寻找合适的存储路径
        //适合Android10
        try {
            uri = resolver.insert(external, values);
        } catch (IllegalArgumentException e) {
            values.remove(MediaStore.Images.Media.RELATIVE_PATH);
            uri = resolver.insert(external, values);
        } finally {
            return uri;
        }
    }

    /**
     * 用户保存任何文件时传入
     *
     * @param context
     * @param saveFileName 文件名
     * @param mineType     文件类型,参考FileIntentUtils类中的getMap
     * @param saveDirName  文件路径
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri saveFileWithAndroidQ(Context context,
                                           String saveFileName,
                                           String mineType,
                                           String saveDirName) {
        Uri uri = null;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mineType);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Download/" + saveDirName);
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        //这里为了避免用户如果删除了Download文件夹，此时让系统帮我们去寻找合适的存储路径
        //适合Android10
        try {
            uri = resolver.insert(external, values);
        } catch (IllegalArgumentException e) {
            values.remove(MediaStore.Images.Media.RELATIVE_PATH);
            uri = resolver.insert(external, values);
        } finally {
            return uri;
        }
    }

    /**
     * 用于将Uri转为File
     *
     * @param uri
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File getFileByUri(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, "(" + MediaStore.Images.ImageColumns.DATA + "=" + "'" + path + "'" + ")", null, null);
                int index = 0;
                int dataIdx = 0;
                for (Objects.requireNonNull(cur).moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index != 0) {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (Objects.requireNonNull(cursor).moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            return new File(Objects.requireNonNull(path));
        }
        return null;
    }


    /**
     * 判断共有文件是否存在
     *
     * @param context
     * @param path
     * @return
     */
    public static boolean isAndroidQFileExists(Context context, String path) {
        AssetFileDescriptor afd;
        ContentResolver cr = context.getContentResolver();
        try {
            Uri uri = Uri.parse(path);
            //r表示读文件  w表示写
            afd = cr.openAssetFileDescriptor(uri, "r");
            if (afd == null) {
                return false;
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * 图片保存
     *
     * @param context      context
     * @param sourceFile   源文件
     * @param saveFileName 保存的文件名
     * @param saveDirName  picture子目录
     * @return 成功或者失败
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean saveImageWithAndroidQ(Context context,
                                                File sourceFile,
                                                String saveFileName,
                                                String saveDirName) {
        if (!isExternalStorageReadable()) {
            throw new RuntimeException("External storage cannot be written!");
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + saveDirName);

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        BufferedInputStream inputStream;
        OutputStream os = null;
        boolean result;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
            result = true;
        } catch (IOException e) {
            result = false;
        }
        return result;
    }


    /**
     * 复制或下载文件到公有目录
     *
     * @param context
     * @param sourcePath
     * @param mimeType
     * @param fileName
     * @param saveDirName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri copyToDownloadAndroidQ(Context context, String sourcePath, String mimeType, String fileName, String saveDirName) {
        if (!isExternalStorageReadable()) {
            throw new RuntimeException("External storage cannot be written!");
        }
        ContentValues values = new ContentValues();
        //显示名称
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        //存储文件的类型
        values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        //公有文件路径
        values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + saveDirName.replaceAll("/", "") + "/");

        //生成一个Uri
        Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();

        //写入
        Uri insertUri = resolver.insert(external, values);
        if (insertUri == null) {
            return null;
        }

        String mFilePath = insertUri.toString();

        InputStream is = null;
        OutputStream os = null;
        try {
            //输出流
            os = resolver.openOutputStream(insertUri);
            if (os == null) {
                return null;
            }
            int read;
            File sourceFile = new File(sourcePath);
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                byte[] buffer = new byte[1444];
                while ((read = is.read(buffer)) != -1) {
                    //写入uri中
                    os.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertUri;

    }


    /**
     * 判断外部存储是否可写入读取
     *
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * 判断外部存储是否可写入
     *
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * 获取私有相册目录
     * 私有目录会被删除
     *
     * @param context
     * @param albumName
     * @return
     */
    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }


    /**
     * 查询指定图片
     *
     * @param context
     * @param path            路径
     * @param environmentType 沙盒类型
     * @param fileName        文件名
     * @return bitmap
     */
    public static Bitmap querySignImageBox(Context context, String path, String environmentType, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        Bitmap bitmap = null;
        //指定沙盒文件夹
        String builder = environmentType.replaceAll("/", "") + "/" +
                path.replaceAll("/", "") + "/" +
                fileName.replaceAll("/", "");
        File picturesFile = context.getExternalFilesDir(builder);
        assert picturesFile != null;
        if (picturesFile.exists()) {
            return BitmapFactory.decodeFile(picturesFile.toString());
        }
        return bitmap;
    }

    /**
     * 删除文件
     *
     * @param context
     * @param fileUri
     */
    public void delete(Context context, Uri fileUri) {
        context.getContentResolver().delete(fileUri, null, null);
        Bundle bundle;
    }


}
