package com.www.net.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Tools
 * @class name：com.business.tools.file_utils
 * @time 2019/12/11 23:04
 * @description 文件工具类
 */
public class FileUtils {

    /**
     * 格式化模板
     */
    public static final String TIME_FORMAT = "_yyyyMMdd_HHmmss";

    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();

    /**
     * 默认本地上传目录
     */
    public static final String UPLOAD_PHOTO_DIR =
            Environment.getExternalStorageDirectory().getPath() + "/a_upload_photos/";

    /**
     * 网页缓存地址
     */
    public static final String WEB_CACHE_DIR =
            Environment.getExternalStorageDirectory().getParent() + "app_web_cache";

    /**
     * 系统相机目录
     */
    public static final String CAMERA_PHOTO_DIR =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getParent();

    public static final String DirName="EasyToolss";


    private static String getTimeFormatName(String timeFormatHeader) {
        Date date = new Date(System.currentTimeMillis());
        //必须加上 单引号
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("'" + timeFormatHeader + "'" + TIME_FORMAT);

        return dateFormat.format(date);
    }


    /**
     * @param timeFormatHeader 格式化的头（除去时间部分）
     * @param extension        后缀名
     * @return 返回时间格式化后的文件名
     */
    public static String getFileNameByTime(String timeFormatHeader, String extension) {
        return getTimeFormatName(timeFormatHeader) + "." + extension;
    }

    public static File createDir(String sdcarDirName) {
        //拼接成 SD 卡中完整的dir
        String dir = SDCARD_DIR + "/" + sdcarDirName + "/";
        File fileDir = new File(dir);

        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        return fileDir;
    }

    public static File createFile(String sdcardDirName, String fileName) {
        return new File(createDir(sdcardDirName), fileName);
    }

    /**
     * 根据时间来创建 相应的文件
     *
     * @param sdcardDirName    文件路径
     * @param timeFormatHeader 格式化的头（除去时间部分）
     * @param extension        后缀名
     * @return file
     */
    public static File createFileByTime(String sdcardDirName, String timeFormatHeader, String extension) {
        final String fileName = getFileNameByTime(timeFormatHeader, extension);
        return createFile(sdcardDirName, fileName);
    }

    /**
     * 获取文件的 MIME
     *
     * @param filePath 文件路径
     * @return MIME
     */
    public String getMimeType(String filePath) {
        final String extension = getExtension(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 获取文件的后缀名
     *
     * @param filePath 文件路径
     * @return 后缀名
     */
    public static String getExtension(String filePath) {
        String suffix = "";
        final File file = new File(filePath);
        final String name = file.getName();
        final int idx = name.lastIndexOf(".");
        if (idx > 0) {
            suffix = name.substring(idx + 1);
        }
        return suffix;
    }


    /**
     * @param mBitmap  Bitmap
     * @param dir      目录名，只需要写自己的相对目录名即可
     * @param compress 压缩比例，100是不压缩，值越小压缩率 越高
     * @return 返回该文件
     */

    public static File saveBitmap(Bitmap mBitmap, String dir, int compress) {
        final String sdStatus = Environment.getExternalStorageState();
        //检测sd是否可用
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        File fileName = createFileByTime(dir, "DOWN_LOAD", "jpg");

        try {
            fos = new FileOutputStream(fileName);
            bos = new BufferedOutputStream(fos);

            //把数据写入到文件
            mBitmap.compress(Bitmap.CompressFormat.JPEG, compress, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                }
                if (bos != null) {
                    bos.close();
                }
                //关闭流
                if (fos != null) {
                    fos.flush();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fileName;
    }

    public static File writeToDisk(InputStream is, String dir, String name) {
        final File file = FileUtils.createFile(dir, name);
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            byte[] data = new byte[1024 * 4];
            int count;
            while ((count = bis.read(data)) != -1) {
                bos.write(data, 0, count);
            }
            bos.flush();
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File writeToDisk(InputStream is, String dir, String prefix, String extension) {
        final File file = FileUtils.createFileByTime(dir, prefix, extension);
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            byte[] data = new byte[1024 * 4];

            int count;
            while ((count = bis.read(data)) != -1) {
                bos.write(data, 0, count);
            }

            bos.flush();
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 通知系统刷新系统相册，是照片展示出来
     */

    private static void refreshDCIM(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            //兼容android 4.4 版本，只扫描存放照面的目录
            MediaScannerConnection.scanFile(context, new String[]{
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()}
                    , null, null);
        } else {
            //扫明真个SD卡 来更新系统图库，当文件很多时，用户体验不佳，而不适合4.4以上版本
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * 读取 raw目录中的文件，并返回为字符串
     */

    public static String getRawFile(int id, Context context) {
        final InputStream is = context.getResources().openRawResource(id);
        final BufferedInputStream bis = new BufferedInputStream(is);
        final InputStreamReader isr = new InputStreamReader(bis);
        final BufferedReader br = new BufferedReader(isr);
        final StringBuilder stringBuilder = new StringBuilder();

        String str;
        try {
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 读取assets 目录下的文件，并返回字符串
     */

    public static String getAssetsFile(String name, Context context) {
        InputStream is = null;
        BufferedInputStream bis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder stringBuilder = null;

        final AssetManager assetManager = context.getAssets();

        try {
            is = assetManager.open(name);
            bis = new BufferedInputStream(is);
            isr = new InputStreamReader(bis);
            br = new BufferedReader(isr);
            stringBuilder = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (is != null) {
                    is.close();
                }
                assetManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (stringBuilder != null) {
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    /**
     * 这个方法是吧Uri 转换成真实路径，也就是photo的path
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        final String scheme = uri.getScheme();
        String date = null;

        if (scheme == null) {
            date = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            date = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            //从内容提供器中 查询数据
            final Cursor cursor = context.getContentResolver().query(uri, new String[]{
                            MediaStore.Images.ImageColumns.DATA}
                    , null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    final int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        date = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return date;
    }







}
