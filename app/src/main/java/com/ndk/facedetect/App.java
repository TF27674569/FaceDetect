package com.ndk.facedetect;

import android.app.Application;

import com.ndk.facedetect.utils.AssetsUtil;

import org.dao.DaoFactory;

/**
 * create by TIAN FENG on 2019/12/10
 */
public class App extends Application {

    private static final int DB_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        AssetsUtil.copyFace(this);

        DaoFactory.Builder
                .app(this)
                .dbName("test.db")
                .dbVersion(DB_VERSION)
                .build();
    }
}
