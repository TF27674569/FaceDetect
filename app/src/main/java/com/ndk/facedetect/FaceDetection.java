package com.ndk.facedetect;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.core.Mat;

/**
 * create by TIAN FENG on 2019/12/4
 */
public class FaceDetection {

    private static final String TAG = "FaceDetection";

    public interface ICollectListener {
        void onSuccess();
    }

    public interface ILabelListener {
        String onLabel(int label);
    }

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }


    private ICollectListener mListener;
    private ILabelListener mLabelListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    /**
     * 检测人脸并保存人脸信息
     *
     * @param mat 当前帧
     */
    public void faceDetection(Mat mat, ILabelListener listener) {
        mLabelListener = listener;
        faceDetection(mat.nativeObj);
    }

    /**
     * 人脸采集
     *
     * @param frame    当前帧
     * @param label    样本id
     * @param labelDir 样本目录
     */
    public void faceCollect(Mat frame, int label, String labelDir) {
        faceCollect(frame.nativeObj, label, labelDir);
    }


    public void setCollectListener(ICollectListener listener) {
        mListener = listener;
    }


    /**
     * 加载人脸识别的分类器文件
     *
     * @param filePath
     */
    public native void loadCascade(String filePath);

    /**
     * 加载样本
     *
     * @param samplePath
     * @param maxSample  最大样本数目
     */
    public native void loadPattern();

    /**
     * 训练样本
     */
    public native void trainPattern(String samplePath, int maxLabel);


    private native void faceDetection(long nativeObj);

    private native void faceCollect(long nativeObj, int label, String labelDir);


    // call back form native
    public void onCollectSuccess() {
        if (mListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onSuccess();
                }
            });
        }
    }


    // 从java获取帧率太低
    public String getNameFormLabel(int label) {
        if (mLabelListener != null) {
            return mLabelListener.onLabel(label);
        }
        return "unknow";
    }

}
