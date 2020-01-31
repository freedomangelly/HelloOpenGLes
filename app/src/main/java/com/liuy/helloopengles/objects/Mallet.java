package com.liuy.helloopengles.objects;

import com.liuy.helloopengles.data.VertexArray;
import com.liuy.helloopengles.programs.ColorShaderProgram;
import com.liuy.helloopengles.util.Consts;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class Mallet {
    private static final int POSITION_COMPONENT_COUNT=2;
    private static final int COLOR_COMPONENT_COUNT=3;
    private static final int STRIDE=(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)* Consts.BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA={
            //x y r g b
            0f,-0.4f,0f,0f,1f,
            0f,0.4f,1f,0f,0f
    };
    private final VertexArray vertexArray;

    public Mallet() {
        this.vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void draw(){
        glDrawArrays(GL_POINTS,0,2);
    }

    public void bindData(ColorShaderProgram textureShaderProgram){
        vertexArray.setVertexAttribPointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,textureShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,STRIDE);
    }

}
