package com.liuy.particles.data

import android.content.Context
import com.liuy.particles.ShaderProgram2
import android.opengl.GLES20.*


/**
 * description:
 * author: freed on 2020/2/5
 * email: 674919909@qq.com
 * version: 1.0
 */
class HeightmapShaderProgram :ShaderProgram2{
    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0
    constructor(context:Context):super(context, com.liuy.particles.R.raw.heightmap_vertex_shader, com.liuy.particles.R.raw.heightmap_fragment_shader){
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }
}