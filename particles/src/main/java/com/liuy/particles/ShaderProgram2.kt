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
        open val U_TIME="u_Time"
        open val A_DIRECTION_VECTIO="a_DirectionVector"
        open val A_PARTICLE_START_TIME="a_ParticleStartTime"
        open val U_VECTOR_TO_LIGHT="u_VecotrToLight"
        open val A_NORMAL="a_Normal"
        open val U_MV_MATRIX = "u_MVMatrix"
        open  val U_IT_MV_MATRIX = "u_IT_MVMatrix"
        open val U_MVP_MATRIX = "u_MVPMatrix"
        open val U_POINT_LIGHT_POSITIONS = "u_PointLightPositions"
        open val U_POINT_LIGHT_COLORS = "u_PointLightColors"
    }


}