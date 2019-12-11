package com.ndk.facedetect.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ndk.facedetect.FaceDetection;
import com.ndk.facedetect.Person;
import com.ndk.facedetect.R;
import com.ndk.facedetect.utils.FileUtils;

import org.dao.DaoFactory;
import org.dao.IDaoSupport;

import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private FaceDetection detection = new FaceDetection();
    private EditText mEditText;
    private Dialog mLoading;

    IDaoSupport<Person> daoSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mEditText = findViewById(R.id.etName);
        mLoading = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("正在训练样本")
                .create();
        daoSupport = DaoFactory.get().getDaoSupportAndCreateTable(Person.class);

    }

    /**
     * 采集
     */
    public void train(View view) {
        String name = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show();
            return;
        }
        int label = FileUtils.currentSampleMaxLable() + 1;
        Person person = new Person(name, label);

        daoSupport.insert(person);

        /**
         * 防止文件重复选择将其 +1
         */
        FaceCollectActivity.startActivity(this, label,false);

    }



    public void train1(View view) {
        String name = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入名称", Toast.LENGTH_SHORT).show();
            return;
        }
        int label = FileUtils.currentSampleMaxLable() + 1;
        Person person = new Person(name, label);

        daoSupport.insert(person);

        /**
         * 防止文件重复选择将其 +1
         */
        FaceCollectActivity.startActivity(this, label,true);
    }


    /**
     * 训练
     */
    public void face(View view) {
        mLoading.show();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                detection.trainPattern(FileUtils.SAMPLE_PATH, FileUtils.currentSampleMaxLable());
                mEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.dismiss();
                        Toast.makeText(MainActivity.this, "样本训练成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 识别
     */
    public void recognizer(View view) {
        detection.loadPattern();
        startActivity(new Intent(this, FaceRecognizerActivity.class));
    }


    public void recognizer1(View view) {
        detection.loadPattern();
        Intent intent = new Intent(this, FaceRecognizerActivity.class);
        intent.putExtra("camera",true);
        startActivity(intent);
    }
}
