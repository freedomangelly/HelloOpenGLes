package com.liuy.helloopengles.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderSource;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ShaderHelper {
    public static String TAG ="ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER,shaderCode);
    }

    public static int compileFragmentShader( String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER,shaderCode);
    }
    //编辑着色器的属性
    private static int compileShader(int glFragmentShader, String shaderCode) {
        int shaderObjectId=glCreateShader(glFragmentShader);//创建新的着色器对象
        if(shaderObjectId == 0){//返回0表示创建失败,也可以通过getError来获得错误码
            Log.e(TAG,"compileShader error "+shaderObjectId);
            return 0;
        }
        glShaderSource(shaderObjectId,shaderCode);//上传源代码，告诉openGL读入字符串，并把它与shaderObjectId所引用的着色器关联起来
        glCompileShader(shaderObjectId);//编译这个着色器
        int[] compileStatus=new int[1];//为了检查是否成功，创建长度为1的数组
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,compileStatus,0);//告诉openGL,把结果存进数组的第一个元素中//验证是否成功
        Log.i(TAG,"results of comiling source:\n"+shaderCode+"\n"+glGetShaderInfoLog(shaderObjectId));
        if(compileStatus[0]==0){//上色失败了
            glDeleteShader(shaderObjectId);
            Log.i(TAG,"compilation of shader failed.");
            return 0;
        }
        return shaderObjectId;
    }
    //将着色器属性与openglse连接
    public static int linkProgram(int vertexShaderId,int fragmentShaderId){
        int programObjectId=glCreateProgram();//创建对象，并把ID存进programObjectId
        if(programObjectId==0){
            Log.i(TAG,"could not create new program");
            return 0;
        }
        //把所有的着色器都赋值到对象上面去
        glAttachShader(programObjectId,vertexShaderId);
        glAttachShader(programObjectId,fragmentShaderId);
        //连接程序
        glLinkProgram(programObjectId);
        int [] linkStatus=new int[1];
        glGetProgramiv(programObjectId,GL_LINK_STATUS,linkStatus,0);//验证连接状态
        Log.i(TAG,"Results of linking program:\n"+glGetProgramInfoLog(programObjectId));
        if(linkStatus[0]==0){//连接失败
            glDeleteShader(programObjectId);
            Log.i(TAG,"linking of shader failed.");
            return 0;
        }
        return programObjectId;
    }
    //验证是否是否有效
    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);//验证当前openGL状态是不是有效的。他也给这个程序对于当前的OPenGL状态是不是有效的，让我们知道为什么当前程序是低效的无法使用的等等
        final int[] validateStatus=new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);//验证是否使用，如果失败了，也可以打出相应的日志
        Log.i(TAG,"Results of validating program:"+validateStatus[0]+"\n"+glGetProgramInfoLog(programObjectId));
        return validateStatus[0]!=0;
    }
}
