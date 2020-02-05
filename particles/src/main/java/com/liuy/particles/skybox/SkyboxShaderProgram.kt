package com.liuy.particles.skybox

import android.content.Context
import android.opengl.GLES20.*
import com.liuy.particles.R
import com.liuy.particles.ShaderProgram2


class SkyboxShaderProgram : ShaderProgram2 {
    private var uMatrixLocation: Int = 0
    private var uTextureUnitLocation: Int = 0
    private var aPositionLocation: Int = 0
    constructor(context: Context):
        super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader){
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId)//GL_TEXTURE_CUBE_MAP立方体贴图纹理
        glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }
}
