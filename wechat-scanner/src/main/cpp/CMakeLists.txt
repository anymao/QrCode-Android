# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("wechat-scanner")

# 注意这里的路径，需要和OpenCV sdk的路径对应
set(ocvlibs "${OpenCV_DIR}/native/libs")
include_directories(${OpenCV_DIR}/native/jni/include)

add_library(libopencv_java4 SHARED IMPORTED)
set_target_properties(libopencv_java4 PROPERTIES
        IMPORTED_LOCATION "${ocvlibs}/${ANDROID_ABI}/libopencv_java4.so")

include_directories(
        opencv2
        wechat
        wechat/detector
        wechat/scale
        wechat/zxing
        wechat/zxing/common
        wechat/zxing/common/binarizer
        wechat/zxing/common/reedsolomon
        wechat/zxing/qrcode
        wechat/zxing/qrcode/detector
        wechat/zxing/qrcode/decoder
)

get_property(dirs DIRECTORY ${CMAKE_SOURCE_DIR} PROPERTY INCLUDE_DIRECTORIES)
message(">>> include_dirs=${dirs}") #打印一下目录情况

aux_source_directory(opencv2 O)
aux_source_directory(wechat W)
aux_source_directory(wechat/detector WD)
aux_source_directory(wechat/scale WS)
aux_source_directory(wechat/zxing WZ)
aux_source_directory(wechat/zxing/common WZC)
aux_source_directory(wechat/zxing/common/binarizer WZCB)
aux_source_directory(wechat/zxing/common/reedsolomon WZCR)
aux_source_directory(wechat/zxing/qrcode WZQ)
aux_source_directory(wechat/zxing/qrcode/decoder WZQD)
aux_source_directory(wechat/zxing/qrcode/detector WZQD2)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        wechat_scanner

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        wechat_scanner.cpp
        ${O}
        ${W}
        ${WD}
        ${WS}
        ${WZ}
        ${WZC}
        ${WZCB}
        ${WZCR}
        ${WZQ}
        ${WZQD}
        ${WZQD2}
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        wechat_scanner

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        libopencv_java4)