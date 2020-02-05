package com.liuy.particles

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.util.Log
import com.liuy.airhockettouch.util.Geometry
import com.liuy.airhockettouch.util.MatrixHelper
import com.liuy.airhockettouch.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 */

class ParticlesRenderer:GLSurfaceView.Renderer{

    var context:Context;
    var projectionMatrix:FloatArray=FloatArray(16)
    var viewMatrix:FloatArray=FloatArray(16)
    var viewProjectionMatrix:FloatArray= FloatArray(16)

    var particleShaderProgram:ParticleShaderProgram? = null
    var particleSystem: ParticleSystem? =null
    var redParticleShooter: ParticleShooter? =null
    var greenParticleShooter: ParticleShooter? =null
    var blueParticleShooter: ParticleShooter? = null
    var globalStartTime:Long = 0

    var texture:Int=0

    constructor(context:Context){
        this.context=context
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清屏为黑色
        glClearColor(0f,0f,0f,0f)
        //绘制类似烟花的效果，粒子越多，就越亮
        //混合技术 公式为 输出=（源因子 * 源片段）+（目标因子 * 目标片段）
        glEnable(GL_BLEND);
        //使用公式为输出=（GL_ONE * 源片段）+（GL_ONE * 目标片段）
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram= ParticleShaderProgram(context)
        particleSystem= ParticleSystem(10000)
        globalStartTime=System.nanoTime()
        var angleVarianceInDegrees:Float = 5f
        var speedVariance:Float = 1f

        val particleDirection:Geometry.Vector= Geometry.Vector(0f,0.5f,0f)
        redParticleShooter= ParticleShooter(Geometry.Point(-1f,0f,0f),Color.rgb(255,50,5),particleDirection,angleVarianceInDegrees, speedVariance)
        greenParticleShooter= ParticleShooter(Geometry.Point(0f,0f,0f),Color.rgb(25,225,25),particleDirection,angleVarianceInDegrees, speedVariance)
        blueParticleShooter= ParticleShooter(Geometry.Point(1f,0f,0f),Color.rgb(5,50,255),particleDirection,angleVarianceInDegrees, speedVariance)

        texture=TextureHelper.loadTexture(context,R.drawable.particle_texture)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0,0,width, height);

        MatrixHelper.perspectiveM(projectionMatrix,45f,width.toFloat()/height.toFloat(),1f,10f,true)
        setIdentityM(viewMatrix,0);
        translateM(viewMatrix,0,0f,-1.5f,-5f)
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        var currentTime:Float=(System.nanoTime()-globalStartTime)/1000000000f
        redParticleShooter!!.addParticles(particleSystem!!,currentTime,5)
        greenParticleShooter!!.addParticles(particleSystem!!,currentTime,5)
        blueParticleShooter!!.addParticles(particleSystem!!,currentTime,5)
        particleShaderProgram!!.useProgram();
        particleShaderProgram!!.setUniforms(viewProjectionMatrix,currentTime,texture)
        particleSystem!!.bindData(particleShaderProgram!!)
        particleSystem!!.draw()
    }




}