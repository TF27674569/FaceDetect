### FaceDetect
[OpenCV基础Demo](https://github.com/TF27674569/OpenCV)</br>
[OpenCV核心功能官网](http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/table_of_content_imgproc/table_of_content_imgproc.html)</br>
####  级联器使用人脸检测
这里采用的时LBP人脸检测，使用OpenCV官方提供的JavaCameraView.java对象进行摄像头的采集，以及处理后的Mat绘制.</br>
加载级联器
```c++
cascadeClassifier.load(filePath);
```
当前帧的回调与处理
```java
    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        faceDetection.faceCollect(inputFrame, mLabel, mLabelDir);
        return inputFrame;
    }
```
NDK层检测人脸，采集在代码中一并处理（源码中有实现逻辑）
```c++
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
 
    // 把脸框出来
    rectangle(*src, faceRect, Scalar(255, 0, 0), 4, LINE_AA);
```
训练样本 ,每个样本的label是不一样的,label是识别后查询的唯一标识,将保存在sd卡的样本读取出并训练保存成xml文件
```c++
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
```
加载样本
```c++
   // 原函数为 model->load(PATTERN_PATH) 但是报了找不到此函数，进去头文件有代码，筛选如下代码即可
 // 加载样本 可以与上面的训练隔开
    FileStorage fs(PATTERN_PATH, FileStorage::READ);
    FileNode fn = fs.getFirstTopLevelNode();
    model->read(fn);
```
识别 label就是人脸录入时的对应关系
```c++
    // 人脸矩形
    Rect faceRect = faces[0];
    Mat face = (*src)(faceRect).clone();
    resize(face, face, Size(128, 128));
    cvtColor(face, face, COLOR_BGRA2GRAY);
    // 直方均衡
    equalizeHist(face,face);

    int label = model->predict(face);
```
