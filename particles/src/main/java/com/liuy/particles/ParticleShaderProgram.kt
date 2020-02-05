package com.liuy.particles

import android.content.Context
import android.opengl.GLES20.*

/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 * 添加粒子系统
 */
class ParticleShaderProgram :ShaderProgram2{
    //Uniform locations
    private var uMatrixLocation:Int=0
    private var uTimeLocation:Int=0
    //Attribute locations
    private var aPositionLocation:Int=0
    private var aColorLocation:Int=0
    private var aDirectionVectorLocation:Int=0
    private var aParticleStartTimeLocation:Int=0

    //纹理
    private var uTextureUnitLocation:Int=0

    constructor(context: Context)
        :super(context,R.raw.particle_vertex_shader,R.raw.particle_fragment_shader){
        uMatrixLocation=glGetUniformLocation(program,U_MATRIX)
        uTimeLocation= glGetUniformLocation(program, U_TIME)

        aPositionLocation= glGetAttribLocation(program, A_POSITION)
        aColorLocation= glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation= glGetAttribLocation(program, A_DIRECTION_VECTIO)
        aParticleStartTimeLocation= glGetAttribLocation(program, A_PARTICLE_START_TIME)

        uTextureUnitLocation= glGetUniformLocation(program, U_TEXTURE_UNIT)
    }

    open fun setUniforms(matrix: FloatArray,elapsedTime :Float,textureId:Int){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0)
        glUniform1f(uTimeLocation,elapsedTime)
        //绑定纹理
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D,textureId)
        glUniform1i(uTextureUnitLocation,0)
    }

    open fun getPoisitionAttributteLocation():Int{
        return aPositionLocation;
    }

    open fun getColorAttributeLocation():Int{
        return aColorLocation;
    }

    open fun getDirectionVectorAttributeLocation():Int{
        return aDirectionVectorLocation;
    }
    open fun getParticleStartTimeAttributeLocation():Int{
        return aParticleStartTimeLocation;
    }
}