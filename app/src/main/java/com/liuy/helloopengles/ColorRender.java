package com.liuy.helloopengles;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.liuy.helloopengles.util.ShaderHelper;
import com.liuy.helloopengles.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES32.GL_QUADS;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ColorRender implements GLSurfaceView.Renderer {
    //记录顶点的变量，一个点有两个变量，x,y
    private static final int POSITION_COMPONENT_COUNT = 2;
    private String TAG = "ColorRender";
    private int color;

    private FloatBuffer vertexBuffer;
    private Context mContext;
    /**
     * 着色器
     */
    private String vertexShaderSource;
    /**
     * 顶点器
     */
    private String fragmentShaderSource;
    /**
     * 着色器对象ID
     */
    int vertexShader;
    /**
     * 顶点器返回id
     */
    int fragmentShader;
    /**
     * 返回连接的id
     */
    int program;

    private static final String U_COLOR="u_Color";//对应着色器中的属性
    private static final String A_Position="a_Position";//对应顶点器中的属性
    /**
     * 着色器uniform位置
     */
    private int uColorLocation;

    /**
     * 顶点器uniform位置
     */
    private int aPositionLocation;
    /**
     * 这些点为要绘制图形的点
     */
    float[] tableVerticesWithTriangles2={

//            //Triangel 1
//            -0.5f,-0.5f,
//            0.5f,0.5f,
//            -0.5f,0.5f,
//            //triangle 2
//            -0.5f,-0.5f,
//            0.5f,-0.5f,
//            0.5f,0.5f,

            0f,0f,
            -0.5f,-0.5f,
            0.5f,-0.5f,
            0.5f,0.5f,
            -0.5f,0.5f,
            -0.5f,-0.5f,
            //Line 1
            -0.5f,0f,
            0.5f,0f,
            //Mallets
            0f,-0.25f,
            0f,0.25f,

//            -0.48f,-0.48f,
//            -0.48f,0.48f,
//            0.48f,0.48f,
//            0.48f,-0.48f,
//            -0.48f,-0.48f,
//            0f,0f

    };
    float[] tableVerticesWithTriangles3={

            0f,0f,1f,1f,1f,
            -0.5f,-0.5f,0.0f,0.7f,0.0f,
            0.5f,-0.5f,0.7f,0.7f,0.0f,
            0.5f,0.5f,0.0f,0.7f,0.7f,
            -0.5f,0.5f,0.7f,0.0f,0.7f,
            -0.5f,-0.5f,0.7f,0.7f,0.0f,

            -0.5f,0f,1f,0f,0f,
            0.5f,0f,1f,0f,0f,

            0f,-0.25f,0f,0f,1f,
            0f,0.25f,1f,0f,0f

    };
    float[] tableVerticesWithTriangles4={

            0f,0f,1f,1f,1f,
            -0.5f,-0.8f,0.0f,0.7f,0.0f,
            0.5f,-0.8f,0.7f,0.7f,0.0f,
            0.5f,0.8f,0.0f,0.7f,0.7f,
            -0.5f,0.8f,0.7f,0.0f,0.7f,
            -0.5f,-0.8f,0.7f,0.7f,0.0f,

            -0.5f,0f,1f,0f,0f,
            0.5f,0f,1f,0f,0f,

            0f,-0.25f,0f,0f,1f,
            0f,0.25f,1f,0f,0f

    };

    private static final String A_COLOR="a_Color";
    private static final int COLOR_COMPONENT_COUNT=3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE=(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;

    private static final String U_MATRIX="u_Matrix";
    private float[] projectionMatrix=new float[16];
    private int uMatrixLocation;


    /**
     * 构造函数
     * @param context
     */
    public ColorRender(Context context) {
        this.mContext=context;
        //读取glsl文件
        vertexShaderSource= TextResourceReader.readTextFileFromResource(mContext,R.raw.simple_vertex_shader);
        fragmentShaderSource= TextResourceReader.readTextFileFromResource(mContext,R.raw.simple_fragemeng_shader);
        Log.i(TAG,"vertexShaderSource="+vertexShaderSource);
        Log.i(TAG,"fragmentShaderSource="+fragmentShaderSource);

        color= Color.WHITE;
        vertexBuffer=ByteBuffer.allocateDirect(tableVerticesWithTriangles4.length * BYTES_PER_FLOAT)//分配和每个点占用4个字节这块内存不会被GC回收
                .order(ByteOrder.nativeOrder())//告诉字节缓冲区，按照贝蒂字节序（navive byte order）组织他的内容；
                .asFloatBuffer();//得到一个可以返回反应底层字节的FloatBuffer类实例
        vertexBuffer.put(tableVerticesWithTriangles4);
        vertexBuffer.position(0);//将点位放回大第一位，否则openGLES读取的时候会从后面读取
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {//创建的时候被调用，但是也可能显示多次
        float redF= Color.red(color)/255;
        float greenF=Color.green(color)/255;
        float blueF=Color.blue(color)/255;
        float alphaF=Color.alpha(color)/255;
        GLES30.glClearColor(redF,greenF,blueF,alphaF);//设置清屏的颜色，格式为ARGB
        glClearColor(0f,0f,0f,0f);
        vertexShader= ShaderHelper.compileVertexShader(vertexShaderSource);
        fragmentShader=ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program=ShaderHelper.linkProgram(vertexShader,fragmentShader);
        Log.i(TAG,"vertexShader="+vertexShader);
        Log.i(TAG,"fragmentShader="+fragmentShader);
        Log.i(TAG,"program="+program);
        ShaderHelper.validateProgram(program);
        glUseProgram(program);
        //获取uniform位置
//        uColorLocation=glGetUniformLocation(program,U_COLOR);
        aColorLocation=glGetAttribLocation(program,A_COLOR);
        aPositionLocation=glGetAttribLocation(program,A_Position);
        Log.i(TAG,"uColorLocation="+uColorLocation);
        Log.i(TAG,"aPositionLocation="+aPositionLocation);
        //关联属性与定点数据的数组
        vertexBuffer.position(0);
        /**int indx,  position位置
         int size,    这是每个属性的数据技术，或者读与这个属性，有多少个分量与每一个定点相关联
         int type,    数据的类型
         boolean normalized,   只有使用整数据数的时候这个参数才有意义
         int stride,        只有当一个数组存储多余一个属性时，他才有意义
         java.nio.Buffer ptr  告诉OpenGL去哪里读取数据。不要忘了他会动缓冲区的当前位置读取，如果没有掉用vertexBuffer.position(0)，它可能尝试读取缓冲区结尾后面的内容
         *
         */
//        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,0,vertexBuffer);
        glVertexAttribPointer(aPositionLocation,POSITION_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexBuffer);
        //最后一步，激活以上的操作
        glEnableVertexAttribArray(aPositionLocation);

        vertexBuffer.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,GL_FLOAT,false,STRIDE,vertexBuffer);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation=glGetUniformLocation(program,U_MATRIX);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {//大小改变的时候被调用
        GLES30.glViewport(0,0,width,height);//设置窗口尺寸
        //添加如下代码
        float aspectRatio=width>height?(float)width/(float)height:(float)height/(float)width;
        /**
         *         float[] m, 目标数组，这个数组的长度至少有16个元素，这样它才能存储正交投影矩阵
         *         int mOffset, 结果矩阵其实的偏移值
         *         float left, x轴最小范围
         *         float right,x轴最大范围
         *         float bottom, y轴最小范围
         *         float top, y轴最大范围
         *         float near, z轴最小范围
         *         float far z轴最大范围
         */

        if(width>height){
            Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
        }else {
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);

        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {//绘制第一帧的是时候被调用
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);//表示清空屏幕，这会擦除屏幕上的所有演示，用之前glClearColor定义的颜色填充整个屏幕
        glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        //绘制分割线
//        glUniform4f(uColorLocation,1f,0f,0f,1f);
        glDrawArrays(GL_LINES,6,2);
        //绘制木槌

        glDrawArrays(GL_POINTS,8,1);
        glDrawArrays(GL_POINTS,9,1);


    }

}
