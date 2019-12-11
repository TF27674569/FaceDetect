#include <jni.h>
#include <string>
#include "opencv2/opencv.hpp"
#include "opencv2/face.hpp"
#include <android/log.h>
#include <dirent.h>

using namespace cv;
using namespace std;
using namespace face;

#define TAG "FACE_TAG"

#define PATTERN_PATH  "/storage/emulated/0/face_tianfeng_pattern.xml"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)


CascadeClassifier cascadeClassifier;
int imageIndex = 0;

// 训练方法
Ptr<FaceRecognizer> model = LBPHFaceRecognizer::create();



extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_facedetect_FaceDetection_loadCascade(JNIEnv *env, jobject instance,
                                                  jstring filePath_) {
    const char *filePath = env->GetStringUTFChars(filePath_, 0);
    cascadeClassifier.load(filePath);
    LOGE("人脸识别级联分类器加载成功");
    env->ReleaseStringUTFChars(filePath_, filePath);
}


/**
 * 从java层获取名字
 */
jstring getNameFormLabel(JNIEnv *env,jobject instance,int label){
    jclass javaClass =  env->GetObjectClass(instance);
    jmethodID  getNameFormLabelId =  env->GetMethodID(javaClass,"getNameFormLabel","(I)Ljava/lang/String;");

    jstring javaName = static_cast<jstring>(env->CallObjectMethod(instance, getNameFormLabelId, label));

    return javaName;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_facedetect_FaceDetection_faceDetection__J(JNIEnv *env, jobject instance,
                                                       jlong nativeObj) {
    Mat *src = reinterpret_cast<Mat *>(nativeObj);

    int width = src->rows;
    int height = src->cols;

    Mat grayMat;
    cvtColor(*src, grayMat, COLOR_BGRA2GRAY);
    std::vector<Rect> faces;
    cascadeClassifier.detectMultiScale(grayMat, faces, 1.1, 3, 0, Size(width / 2, height / 2));
    LOGE("人脸size = %d", faces.size());

    if (faces.size() != 1) {
        return;
    }

    // 人脸矩形
    Rect faceRect = faces[0];
    Mat face = (*src)(faceRect).clone();
    resize(face, face, Size(128, 128));
    cvtColor(face, face, COLOR_BGRA2GRAY);
    // 直方均衡
    equalizeHist(face,face);

    int label = model->predict(face);

    // TODO  样本匹配放在移动端实在太慢了
    // 前40个是样本
    if (label<41){
        // 其他样本
        putText(*src, "unkonw", Point(faceRect.x + 20, faceRect.y - 20),
                HersheyFonts::FONT_HERSHEY_COMPLEX, 1, Scalar(255, 0, 0, 255), 2, LINE_AA);
    } else {
        // 从java获取样本名字
        jstring  name = getNameFormLabel(env,instance,label);
        const char *javaName = env->GetStringUTFChars(name, 0);
        putText(*src, javaName, Point(faceRect.x + 20, faceRect.y - 20),
                HersheyFonts::FONT_HERSHEY_COMPLEX, 1, Scalar(255, 0, 0, 255), 2, LINE_AA);
        env->ReleaseStringUTFChars(name,javaName);
    }


    // 把脸框出来
    rectangle(*src, faceRect, Scalar(255, 0, 0), 4, LINE_AA);
}



void callJava(JNIEnv *env,jobject instance){
    jclass faceDetectionClass =  env->GetObjectClass(instance);
    jmethodID  methodId = env->GetMethodID(faceDetectionClass,"onCollectSuccess","()V");
    env->CallVoidMethod(instance,methodId);
}


/**
 * 采集人脸
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_facedetect_FaceDetection_faceCollect__JILjava_lang_String_2(JNIEnv *env,
                                                                         jobject instance,
                                                                         jlong nativeObj,
                                                                         jint label,
                                                                         jstring labelDir_) {
    // 只取10 张
    if (imageIndex == 30) {
        LOGE("采集成功");
        callJava(env,instance);
        return;
    }
    const char *labelDir = env->GetStringUTFChars(labelDir_, 0);

    Mat *src = reinterpret_cast<Mat *>(nativeObj);
    int width = src->rows;
    int height = src->cols;

    Mat grayMat;
    cvtColor(*src, grayMat, COLOR_BGRA2GRAY);
    vector<Rect> faces;
    cascadeClassifier.detectMultiScale(grayMat, faces, 1.1, 3, 0, Size(width / 2, height / 2));
    LOGE("人脸size = %d", faces.size());

    if (faces.size() != 1) {
        return;
    }
    imageIndex++;

    // 人脸矩形
    Rect faceRect = faces[0];

    // 三张取一张
    if (imageIndex%3==0){

        Mat face = (*src)(faceRect).clone();
        resize(face, face, Size(128, 128));
        cvtColor(face, face, COLOR_BGRA2GRAY);

        // 直方均衡 我之前在得到的效果图很暗 均衡后提高下对比度
        equalizeHist(face,face);

        String path = format("%s/%d.pgm", labelDir, imageIndex/3);

        if (imwrite(path, face)) {
            LOGE("imwrit success :%s", path.c_str());
        }
    }

    rectangle(*src, faceRect, Scalar(255, 0, 0), 4, LINE_AA);

    env->ReleaseStringUTFChars(labelDir_, labelDir);
}


/**
 * 加载样本
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_facedetect_FaceDetection_loadPattern(JNIEnv *env, jobject instance) {

    // 加载样本 可以与上面的训练隔开
    FileStorage fs(PATTERN_PATH, FileStorage::READ);
    FileNode fn = fs.getFirstTopLevelNode();
    model->read(fn);
    LOGE("加载成功");

}


/**
 * 训练
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_facedetect_FaceDetection_trainPattern(JNIEnv *env, jobject instance,
                                                   jstring samplePath_, jint maxLabel) {
    const char *samplePath = env->GetStringUTFChars(samplePath_, 0);

    //model->load(PATTERN_PATH);
    // 训练样本
    vector<Mat> faces;
    vector<int> labels;


    // 样本 TODO 从30开始 太多了识别时比较慢
    for (int i = 30; i <= maxLabel; ++i) {
        for (int j = 1; j <= 10; ++j) {
            // 样本文件路径
            String face_path = format("%s/s%d/%d.pgm", samplePath,i, j);
            // 读取样本
            Mat face = imread(face_path);
            if (face.empty()) {
                LOGE("face mat is empty");
                continue;
            }

            // LBPH检测 model->train 参数需要灰度图
            cvtColor(face,face,COLOR_BGRA2GRAY);

            // 确保大小一致
            resize(face, face, Size(128, 128));
            faces.push_back(face);
            labels.push_back(i);
            LOGE("face path： %s",face_path.c_str());
        }
    }

    // 同一个人 label 一样
    model->train(faces, labels);
    // 训练样本是 xml ，本地
    model->save(PATTERN_PATH);// 存的是处理的特征数据
    LOGE("样本训练成功");

    env->ReleaseStringUTFChars(samplePath_, samplePath);
}