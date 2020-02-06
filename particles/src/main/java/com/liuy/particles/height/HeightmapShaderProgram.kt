package com.liuy.particles.height

import android.content.Context
import android.opengl.GLES20.*
import com.liuy.particles.ShaderProgram2


/**
 * description:
 * author: freed on 2020/2/5
 * email: 674919909@qq.com
 * version: 1.0
 */
class HeightmapShaderProgram :ShaderProgram2{
//    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0
    //光源添加参数
    private var uVectorToLightLocation = 0
    private var aNormalLocation = 0

    private var uMVMatrixLocation = 0
    private var uIT_MVMatrixLocation = 0
    private var uMVPMatrixLocation = 0
    private var uPointLightPositionsLocation = 0
    private var uPointLightColorsLocation = 0

    constructor(context:Context):super(context, com.liuy.particles.R.raw.heightmap_vertex_shader, com.liuy.particles.R.raw.heightmap_fragment_shader){
//        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);

        //眼世界添加
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uPointLightPositionsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
    }

    /**
     *

    fun setUniforms(matrix: FloatArray,vectorToLight:Geometry.Vector) {
//        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform3f(uVectorToLightLocation, vectorToLight.x,vectorToLight.y,vectorToLight.z);
    }
     */

    fun setUniforms(mvMatrix: FloatArray?,
                    it_mvMatrix: FloatArray?,
                    mvpMatrix: FloatArray?,
                    vectorToDirectionalLight: FloatArray?,
                    pointLightPositions: FloatArray?,
                    pointLightColors: FloatArray?) {
        //这几行代码把所有矩阵传递给着色器。
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        //传递方向光的向量给着色器，
        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)
        //把光的位置和颜色传给着色器，我们把着色器中国男的最后两个uniform定义为有三个向量的数组，因此，对于每个uniform我们调用glUniform*fv()时，都把第二个参数设为3，3是计数。这告诉OpenGL需要从数组中为那个uniform读入3个向量
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }


    fun getNormalAttributeLocation(): Int {
        return aNormalLocation
    }
}