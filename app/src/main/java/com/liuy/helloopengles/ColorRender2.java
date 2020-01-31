package com.liuy.helloopengles;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.liuy.helloopengles.programs.ColorShaderProgram;
import com.liuy.helloopengles.objects.Mallet;
import com.liuy.helloopengles.util.MatrixHelper;
import com.liuy.helloopengles.objects.Table;
import com.liuy.helloopengles.util.TextureHelper;
import com.liuy.helloopengles.programs.TextureShaderProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ColorRender2 implements GLSurfaceView.Renderer {
    private Context mContext;
    private float[] projectionMatrix=new float[16];
    private float[] modelMatrix=new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    /**
     * 构造函数
     * @param context
     */
    public ColorRender2(Context context) {
        this.mContext=context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {//创建的时候被调用，但是也可能显示多次
        glClearColor(0f,0f,0f,0f);
        table=new Table();
        mallet=new Mallet();
        textureShaderProgram=new TextureShaderProgram(mContext);
        colorShaderProgram=new ColorShaderProgram(mContext);

        texture= TextureHelper.loadTexture(mContext,R.drawable.air_hockey_surface);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {//大小改变的时候被调用
        GLES30.glViewport(0,0,width,height);//设置窗口尺寸


//        Matrix.translateM(modelMatrix,0,0f,0f,-2f);//像Z轴平移2个
        //用45度角的视野创建一个投影模式，这个是椎体从z值为-1的位置开始，在z值为-10的位置结束
        MatrixHelper.perspectiveM(projectionMatrix,45,(float) width/(float) height,1f,10f,false);
        Matrix.setIdentityM(modelMatrix,0);//设置为单位矩阵
        //直接设置完这句后会导致界面变黑，所以添加下面的变量
//        //创建变量存储临时结果
        float[] temp=new float[16];

        Matrix.translateM(modelMatrix,0,0f,0f,-2.5f);
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        //把投影矩阵和模型矩阵相乘
        Matrix.multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        //把结果copy到projectionMatrix
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);


//        //添加如下代码
//        float aspectRatio=width>height?(float)width/(float)height:(float)height/(float)width;
//        /**
//         *         float[] m, 目标数组，这个数组的长度至少有16个元素，这样它才能存储正交投影矩阵
//         *         int mOffset, 结果矩阵其实的偏移值
//         *         float left, x轴最小范围
//         *         float right,x轴最大范围
//         *         float bottom, y轴最小范围
//         *         float top, y轴最大范围
//         *         float near, z轴最小范围
//         *         float far z轴最大范围
//         */
//
//        if(width>height){
//            Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
//        }else {
//            Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
//
//        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {//绘制第一帧的是时候被调用
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);//表示清空屏幕，这会擦除屏幕上的所有演示，用之前glClearColor定义的颜色填充整个屏幕
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectionMatrix,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }

}
