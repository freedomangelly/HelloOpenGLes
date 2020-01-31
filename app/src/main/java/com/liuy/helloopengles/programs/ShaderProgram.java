package com.liuy.helloopengles.programs;

import android.content.Context;

import com.liuy.helloopengles.util.ShaderHelper;
import com.liuy.helloopengles.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ShaderProgram {
    static String U_MATRIX="u_Matrix";
    static String U_TEXTURE_UNIT="u_TextureUnit";

    static String A_POSITION="a_Position";
    static String A_COLOR="a_Color";
    static String A_TEXTURE_COORDINATES="a_TextureCoordinates";
    static String U_COLOR="u_Color";

    int program;

    public ShaderProgram(Context context,int vertexShaderResourceId,
	int fragmentShaderResourceId) {
        program= ShaderHelper.buildProgram(
		TextResourceReader
		.readTextFileFromResource(context,vertexShaderResourceId),
		TextResourceReader
		.readTextFileFromResource(context,fragmentShaderResourceId));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
