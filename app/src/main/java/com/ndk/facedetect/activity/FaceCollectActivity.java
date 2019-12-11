package com.ndk.facedetect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ndk.facedetect.FaceDetection;
import com.ndk.facedetect.utils.FileUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;


/**
 * 采集人脸样本
 * create by TIAN FENG on 2019/12/9
 */
public class FaceCollectActivity extends BaseFaceActivity implements FaceDetection.ICollectListener {

    public static void startActivity(Context context, int id,boolean isBack) {
        Intent intent = new Intent(context, FaceCollectActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("camera", isBack);
        context.startActivity(intent);
    }

    private int mLabel;
    private boolean isBack;
    private String mLabelDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLabel = getIntent().getIntExtra("id", -1);
        isBack = getIntent().getBooleanExtra("camera",false);
        super.onCreate(savedInstanceState);
        if (mLabel == -1) {
            throw new IllegalArgumentException("id is null!");
        }
        mLabelDir = FileUtils.getLabelDir(mLabel);

        faceDetection.setCollectListener(this);
    }

    protected int getCameraId() {
        return isBack? CameraBridgeViewBase.CAMERA_ID_BACK:CameraBridgeViewBase.CAMERA_ID_FRONT;
    }


    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        faceDetection.faceCollect(inputFrame, mLabel, mLabelDir);
        return inputFrame;
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "采集成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
