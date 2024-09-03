package com.lirui.lib_common.util;

import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Wu Jingyu
 * Date: 2015/10/12
 * Time: 13:54
 */
public class FileTypeUtil {
    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    static {
        mFileTypes.put("ffd8ff", "jpg"); //JPEG (jpg)
        mFileTypes.put("89504e", "png"); //PNG (png)
        mFileTypes.put("474946", "gif"); //GIF (gif)
        mFileTypes.put("49492a", "tif"); //TIFF (tif)
        mFileTypes.put("424d22", "bmp"); //16色位图(bmp)
        mFileTypes.put("424d82", "bmp"); //24位位图(bmp)
        mFileTypes.put("424d8e", "bmp"); //256色位图(bmp)
        mFileTypes.put("414331", "dwg"); //CAD (dwg)
        mFileTypes.put("3c2144", "html"); //HTML (html)
        mFileTypes.put("3c2164", "htm"); //HTM (htm)
        mFileTypes.put("48544d", "css"); //css
        mFileTypes.put("696b2e", "js"); //js
        mFileTypes.put("7b5c72", "rtf"); //Rich Text Format (rtf)
        mFileTypes.put("384250", "psd"); //Photoshop (psd)
        mFileTypes.put("46726f", "eml"); //Email [Outlook Express 6] (eml)
        mFileTypes.put("d0cf11", "doc"); //MS Excel 注意：word、msi 和 excel的文件头一样
        mFileTypes.put("d0cf11", "vsd"); //Visio 绘图
        mFileTypes.put("537461", "mdb"); //MS Access (mdb)
        mFileTypes.put("252150", "ps");
        mFileTypes.put("255044", "pdf"); //Adobe Acrobat (pdf)
        mFileTypes.put("2e524d", "rmvb"); //rmvb/rm相同
        mFileTypes.put("464c56", "flv"); //flv与f4v相同
        mFileTypes.put("000000", "mp4");
        mFileTypes.put("494433", "mp3");
        mFileTypes.put("000001", "mpg"); //
        mFileTypes.put("3026b2", "wmv"); //wmv与asf相同
        mFileTypes.put("524946", "wav"); //Wave (wav)
        mFileTypes.put("524946", "avi");
        mFileTypes.put("4d5468", "mid"); //MIDI (mid)
        mFileTypes.put("504b03", "zip");
        mFileTypes.put("526172", "rar");
        mFileTypes.put("235468", "ini");
        mFileTypes.put("504b03", "jar");
        mFileTypes.put("4d5a90", "exe");//可执行文件
        mFileTypes.put("3c2540", "jsp");//jsp文件
        mFileTypes.put("4d616e", "mf");//MF文件
        mFileTypes.put("3c3f78", "xml");//xml文件
        mFileTypes.put("494e53", "sql");//xml文件
        mFileTypes.put("706163", "java");//java文件
        mFileTypes.put("406563", "bat");//bat文件
        mFileTypes.put("1f8b08", "gz");//gz文件
        mFileTypes.put("6c6f67", "properties");//bat文件
        mFileTypes.put("cafeba", "class");//bat文件
        mFileTypes.put("495453", "chm");//bat文件
        mFileTypes.put("040000", "mxp");//bat文件
        mFileTypes.put("504b03", "docx");//docx文件
        mFileTypes.put("d0cf11", "wps");//WPS文字wps、表格et、演示dps都是一样的
        mFileTypes.put("643130", "torrent");


        mFileTypes.put("6d6f6f", "mov"); //Quicktime (mov)
        mFileTypes.put("ff5750", "wpd"); //WordPerfect (wpd)
        mFileTypes.put("cfad12", "dbx"); //Outlook Express (dbx)
        mFileTypes.put("214244", "pst"); //Outlook (pst)
        mFileTypes.put("ac9eed", "qdf"); //Quicken (qdf)
        mFileTypes.put("e38285", "pwl"); //Windows Password (pwl)
        mFileTypes.put("2e7261", "ram"); //Real Audio (ram)

    }

    public static boolean isImageFile(String filePath) {
        String fileType = mFileTypes.get(getFileHeader(filePath));
        if (!TextUtils.isEmpty(fileType) && (fileType.equals("jpg") || fileType.equals("png") || fileType.equals("gif")
                || fileType.equals("bmp"))) {
            return true;
        }
        return false;
    }

    public static String getFileType(String filePath) {
        return mFileTypes.get(getFileHeader(filePath));
    }

    //获取文件头信息
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toLowerCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
