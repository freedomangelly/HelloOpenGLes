package com.liuy.helloopengles.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_SAMPLER_2D;
import static android.opengl.GLES20.GL_TEXTURE20;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetTexParameterfv;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class TextureHelper {
    static String TAG="TextureHelper";
    public static int loadTexture(Context context,int resourceId){
        //创建纹理对象
        int [] textureObjectIds=new int[1];
        glGenTextures(1,textureObjectIds,0);//建立一个纹理对象
        if(textureObjectIds[0]==0){ //检测是否成功
            Log.i(TAG,"could not generate a new OpenGL texture object.");
            return 0;
        }
        //android不能直接读png jpg转换为位图
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;//不进行缩放
        Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),resourceId,options);//加载位图
        if(bitmap==null){
            Log.i(TAG,"resource id "+resourceId+" could not be decoded.");
            glDeleteTextures(1,textureObjectIds,0);
            return 0;
        }
        //告诉新生成的纹理对象
        //第一个参数告诉opengl这个纹理作为一个二维纹理对待，第二个参数是要绑定的纹理id
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0]);
        //纹理的过滤
        //当纹理被缩小或者扩大的时候，我们需要通过纹理过滤来说明会发生什么。
        //设置默认的纹理过滤参数

        glTexParameteri(GL_TEXTURE_2D,
		GL_TEXTURE_MIN_FILTER,
		GL_LINEAR_MIPMAP_LINEAR);
		        glTexParameteri(GL_TEXTURE_2D,
		GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        //加载位图数据到opengl
        texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        
        //生成所需必要的级别
        glGenerateMipmap(GL_TEXTURE_2D);
        //绑定这个纹理
		bitmap.recycle();//是否数据
        glBindTexture(GL_TEXTURE_2D,0);
        return textureObjectIds[0];
    }
//    public static int loadTexture(Context context, int resourceId) {
//        final int[] textureObjectIds = new int[1];
//        glGenTextures(1, textureObjectIds, 0);
//
//        if (textureObjectIds[0] == 0) {
//                Log.w(TAG, "Could not generate a new OpenGL texture object.");
//            return 0;
//        }
//
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
//
//        // Read in the resource
//        final Bitmap bitmap = BitmapFactory.decodeResource(
//                context.getResources(), resourceId, options);
//
//        if (bitmap == null) {
//                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
//
//            glDeleteTextures(1, textureObjectIds, 0);
//            return 0;
//        }
//        // Bind to the texture in OpenGL
//        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
//
//        // Set filtering: a default must be set, or the texture will be
//        // black.
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        // Load the bitmap into the bound texture.
//        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
//
//        // Note: Following code may cause an error to be reported in the
//        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
//        // Failed to generate texture mipmap levels (error=3)
//        // No OpenGL error will be encountered (glGetError() will return
//        // 0). If this happens, just squash the source image to be
//        // square. It will look the same because of texture coordinates,
//        // and mipmap generation will work.
//
//        glGenerateMipmap(GL_TEXTURE_2D);
//
//        // Recycle the bitmap, since its data has been loaded into
//        // OpenGL.
//        bitmap.recycle();
//
//        // Unbind from the texture.
//        glBindTexture(GL_TEXTURE_2D, 0);
//
//        return textureObjectIds[0];
//    }
}
