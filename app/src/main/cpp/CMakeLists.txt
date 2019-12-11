
cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#判断编译器类型,如果是gcc编译器,则在编译选项中加入c++11支持
if(CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "-std=c++11 ${CMAKE_CXX_FLAGS}")
    message(STATUS "optional:-std=c++11")
endif(CMAKE_COMPILER_IS_GNUCXX)

#把当前目录作为源文件目录赋值给 SRC_LIST
AUX_SOURCE_DIRECTORY(../cpp SRC_LIST)

#需要引入我们头文件,以这个配置的目录为基准
include_directories(../jniLibs/include)


# 添加依赖 opencv.so 库
add_library(
        opencv_java3
        SHARED
        IMPORTED)
set_target_properties(
        opencv_java3
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/armeabi/libopencv_java3.so)

add_library(
             native-lib
             SHARED
            ${SRC_LIST})

find_library(
              log-lib
              log )



target_link_libraries(
        native-lib
        opencv_java3
        jnigraphics
        ${log-lib} )