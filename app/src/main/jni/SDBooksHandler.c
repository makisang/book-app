#include "com_raider_book_utils_JniUtils.h"

/*
 * Class:     com_raider_book_utils_JniUtils
 * Method:    storeBooksToDB
 * Signature: ()Z
 */
JNIEXPORT jstring JNICALL Java_com_raider_book_utils_JniUtils_storeBooksToDB
        (JNIEnv *env, jobject jObj){
    return (*env)->NewStringUTF(env, "jni test");
}
