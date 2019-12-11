package com.ndk.facedetect.utils;

import android.os.Environment;

import java.io.File;

/**
 * create by TIAN FENG on 2019/12/10
 */
public class FileUtils {

    /**
     * 样本存储路径
     */
    public static final String SAMPLE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "face_sample";

    /**
     * 当前样本最大的lable 为了防止重复，后面处理的lable值只能比这个值大
     */
    public static int currentSampleMaxLable() {
        File file = new File(SAMPLE_PATH);
        return file.list().length;
    }

    /**
     * 创建并返回当前label的路径
     */
    public static String getLabelDir(int label) {
        File file = new File(SAMPLE_PATH + "/s" + label);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

}
