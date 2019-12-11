package com.ndk.facedetect.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ndk.facedetect.FaceDetection;
import com.ndk.facedetect.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * create by TIAN FENG on 2019/12/9
 */
public abstract class BaseFaceActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {
    private JavaCameraView mOpenCvCameraView;
    protected FaceDetection faceDetection;
    private File mCascadeFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        // 处理摄像头为前置摄像头
        mOpenCvCameraView.setCameraIndex(getCameraId());
        // 分辨率640*480  提高帧率
        mOpenCvCameraView.setMaxFrameSize(640, 480);

        copyCascadeFile();
        faceDetection = new FaceDetection();
        // 加载级联器
        faceDetection.loadCascade(mCascadeFile.getAbsolutePath());
    }

    protected int getCameraId() {
        return CameraBridgeViewBase.CAMERA_ID_BACK;
    }


    private void copyCascadeFile() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            if (mCascadeFile.exists()) return;
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOpenCvCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

}
