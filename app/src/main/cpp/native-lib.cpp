#include <jni.h>
#include <string>
#include "com_liuy_helloopengles_NativeRender.h"
#include <EGL/egl.h>
#include "GLES3/gl3.h"

//int JNI_OnLoad(JavaVM *vm,void *reserved){
//    JNIEnv *env=NULL;
//    if(vm->GetEnv((void **)&env,JNI_VERSION_1_6)!=JNI_OK){
//        return -1;
//    }
//
//}

/*
 * Class:     com_liuy_helloopengles_NativeRender
 * Method:    surfaceCreated
 * Signature: (I)V
 */
extern "C" JNIEXPORT void JNICALL Java_com_liuy_helloopengles_NativeRender_surfaceCreated
        (JNIEnv *env, jobject jobject1, jint color){
    GLfloat redF=((color>>16)&0xFF) * 1.0f/255;
    GLfloat greenF=((color>>8)&0xFF) * 1.0f/255;
    GLfloat blueF=(color&0xFF) * 1.0f/255;
    GLfloat alpaF=((color>>24)&0xFF) * 1.0f/255;
    glClearColor(redF,greenF,blueF,alpaF);
}

/*
 * Class:     com_liuy_helloopengles_NativeRender
 * Method:    sufaceChange
 * Signature: (II)V
 */
extern "C" JNIEXPORT void JNICALL Java_com_liuy_helloopengles_NativeRender_sufaceChange
        (JNIEnv *env, jobject object, jint width, jint height){
    glViewport(0,0,width,height);
}

/*
 * Class:     com_liuy_helloopengles_NativeRender
 * Method:    onDrawFrame
 * Signature: ()V
 */
extern "C" JNIEXPORT void JNICALL Java_com_liuy_helloopengles_NativeRender_onDrawFrame
        (JNIEnv *env, jobject object){
    glClear(GL_COLOR_BUFFER_BIT);
}
