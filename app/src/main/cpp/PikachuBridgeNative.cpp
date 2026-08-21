#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

#define LOG_TAG "PikachuBridgeNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_Pikachu_owner_engine_VisualDetectionEngine_processFrameNative(
        JNIEnv* env,
        jobject thiz,
        jbyteArray frameData,
        jint width,
        jint height) {

    if (frameData == NULL) {
        return JNI_FALSE;
    }

    jsize length = env->GetArrayLength(frameData);
    if (length <= 0) {
        return JNI_FALSE;
    }

    // পিক্সেল ডেটা প্রসেসিং
    jbyte* buffer = env->GetByteArrayElements(frameData, NULL);

    // মেমোরি আনলক
    env->ReleaseByteArrayElements(frameData, buffer, JNI_ABORT);

    return JNI_TRUE;
}

}
