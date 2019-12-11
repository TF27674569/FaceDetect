package com.ndk.facedetect.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * create by TIAN FENG on 2019/12/10
 */
public class AssetsUtil {

    private static final String TAG = "AssetsUtil";

    public static void copyFace(Context context) {
        AssetManager assetManager = context.getAssets();

        String[] dirs = {};
        try {
            dirs = assetManager.list("sample");
        } catch (IOException e) {
            Log.e(TAG, "assetManager list sample error! ", e);
        }
        for (String dir : dirs) {
            try {
                String[] files = assetManager.list("sample/" + dir);
                InputStream in = null;
                OutputStream out = null;
                for (String fileName : files) {
                    in = assetManager.open("sample/" + dir + "/" + fileName);

                    // 样本 子路经
                    File file = new File(FileUtils.SAMPLE_PATH+"/"+dir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    // 样本
                    File outFile = new File(file.getAbsolutePath(), fileName);
                    if (outFile.exists()) {
                        Log.d(TAG, "file exist.......");
                        continue;
                    }

                    out = new FileOutputStream(outFile);
                    copyFile(in, out);

                }
            } catch (IOException e) {
                Log.e(TAG, "assetManager list sample/" + dir + "  error! ", e);
            }

        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
