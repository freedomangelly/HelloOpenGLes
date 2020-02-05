package com.liuy.particles

import android.content.Context
import com.liuy.airhockettouch.programs.ShaderProgram

/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 */
open class ShaderProgram2( context: Context, vertexShaderResourceId:Int,
                           fragmentShaderResourceId:Int): ShaderProgram( context, vertexShaderResourceId,
         fragmentShaderResourceId) {
    companion object{
        val U_TIME="u_Time"
        val A_DIRECTION_VECTIO="a_DirectionVector"
        val A_PARTICLE_START_TIME="a_ParticleStartTime"
    }


}