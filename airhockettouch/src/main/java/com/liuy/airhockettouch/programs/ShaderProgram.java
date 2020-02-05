package com.liuy.airhockettouch.programs;

import android.content.Context;

import com.liuy.airhockettouch.util.ShaderHelper;
import com.liuy.airhockettouch.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ShaderProgram {
    public static String U_MATRIX="u_Matrix";
    public static String U_TEXTURE_UNIT="u_TextureUnit";

    public static String A_POSITION="a_Position";
    public static String A_COLOR="a_Color";
    public static String A_TEXTURE_COORDINATES="a_TextureCoordinates";
    public static String U_COLOR="u_Color";

    public int program;

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
