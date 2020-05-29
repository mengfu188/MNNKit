package com.alibaba.android.mnnkit.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Common {
    public static String TAG = "MNNKitDemo";

    public static void copyAssetResource2File(Context context, String assetsFile, String outFile) throws IOException {
        InputStream is = context.getAssets().open(assetsFile);
        File outF = new File(outFile);
        FileOutputStream fos = new FileOutputStream(outF);

        int byteCount;
        byte[] buffer = new byte[1024];
        while ((byteCount = is.read(buffer)) != -1) {
            fos.write(buffer, 0, byteCount);
        }
        fos.flush();
        is.close();
        fos.close();
        outF.setReadable(true);
    }

    public static Bitmap readImageFromAsset(AssetManager asset, String fileName) {
        Bitmap bitmap = null;
        InputStream ims = null;
        try {
            ims = asset.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap =  BitmapFactory.decodeStream(ims);
        Log.d(TAG, "bitmap config is " + bitmap.getConfig().toString());
        return bitmap;
    }

    public static String read(File file){
        try {
            FileInputStream fis=new FileInputStream(file);
            BufferedInputStream bis=new BufferedInputStream(fis);
            String content="";
            //自定义缓冲区
            byte[] buffer=new byte[10240];
            int flag=0;
            while((flag=bis.read(buffer))!=-1){
                content+=new String(buffer, 0, flag);
            }
//            System.out.println(content);
            //关闭的时候只需要关闭最外层的流就行了
            bis.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean write(File file, String content){
        try {
//            File file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(file);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
//            String content="xxxxxxxxx！";
            bos.write(content.getBytes(),0,content.getBytes().length);
            bos.flush();
            bos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
