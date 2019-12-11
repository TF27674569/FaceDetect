package com.ndk.facedetect.activity;

import android.os.Bundle;

import com.ndk.facedetect.FaceDetection;
import com.ndk.facedetect.Person;

import org.dao.DaoFactory;
import org.dao.IDaoSupport;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.List;

/**
 * create by TIAN FENG on 2019/12/10
 */
public class FaceRecognizerActivity extends BaseFaceActivity implements FaceDetection.ILabelListener {

    private  List<Person> mPersons;
    private boolean isBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isBack = getIntent().getBooleanExtra("camera",false);
        super.onCreate(savedInstanceState);
        IDaoSupport<Person> daoSupport = DaoFactory.get().getDaoSupportAndCreateTable(Person.class);
        mPersons = daoSupport.querySupport().queryAll();
    }

    protected int getCameraId() {
        return isBack?CameraBridgeViewBase.CAMERA_ID_BACK:CameraBridgeViewBase.CAMERA_ID_FRONT;
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        faceDetection.faceDetection(inputFrame,this);
        return inputFrame;
    }

    @Override
    public String onLabel(int label) {
        String name = mPersons.get(label - 40).getName();
        return name;
    }
}
