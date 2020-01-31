package com.liuy.helloopengles.programs;

import android.content.Context;

import com.liuy.helloopengles.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ColorShaderProgram extends  ShaderProgram{

    private final int uMatrixLocation;
    private final int aColorLocation;
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,R.raw.simple_fragemeng_shader);
        uMatrixLocation=glGetUniformLocation(program,U_MATRIX);
        aPositionLocation=glGetAttribLocation(program,A_POSITION);
        aColorLocation=glGetAttribLocation(program,A_COLOR);
    }

    public void setUniforms(float[] matrix){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
