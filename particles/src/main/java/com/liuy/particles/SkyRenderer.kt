package com.liuy.particles

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import com.liuy.airhockettouch.util.Geometry
import com.liuy.airhockettouch.util.MatrixHelper
import com.liuy.airhockettouch.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.setIdentityM
import android.view.View
import android.view.MotionEvent
import android.view.View.OnTouchListener


/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 */

class SkyRenderer : GLSurfaceView.Renderer {

    var context: Context;
    var projectionMatrix: FloatArray = FloatArray(16)
    var viewMatrix: FloatArray = FloatArray(16)
    var viewProjectionMatrix: FloatArray = FloatArray(16)

    var particleShaderProgram: ParticleShaderProgram? = null
    var particleSystem: ParticleSystem? = null
    var redParticleShooter: ParticleShooter? = null
    var greenParticleShooter: ParticleShooter? = null
    var blueParticleShooter: ParticleShooter? = null
    var globalStartTime: Long = 0

    var texture: Int = 0

    //天空盒子参数
    private var skyboxProgram: SkyboxShaderProgram? = null
    private var skybox: SkyBox? = null
    private var skyboxTexture: Int = 0
    private var xRotation: Float = 0.toFloat()
    private var yRotation: Float = 0.toFloat()
    private var particleTexture: Int = 0

    constructor(context: Context) {
        this.context = context
    }

    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清屏为黑色
        glClearColor(0f, 0f, 0f, 0f)
        //绘制类似烟花的效果，粒子越多，就越亮
        //混合技术 公式为 输出=（源因子 * 源片段）+（目标因子 * 目标片段）
//        glEnable(GL_BLEND);
//        //使用公式为输出=（GL_ONE * 源片段）+（GL_ONE * 目标片段）
//        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()
        var angleVarianceInDegrees: Float = 5f
        var speedVariance: Float = 1f

        val particleDirection = Geometry.Vector(0f, 0.5f, 0f)
        redParticleShooter = ParticleShooter(Geometry.Point(-1f, 0f, 0f), Color.rgb(255, 50, 5), particleDirection, angleVarianceInDegrees, speedVariance)
        greenParticleShooter = ParticleShooter(Geometry.Point(0f, 0f, 0f), Color.rgb(25, 225, 25), particleDirection, angleVarianceInDegrees, speedVariance)
        blueParticleShooter = ParticleShooter(Geometry.Point(1f, 0f, 0f), Color.rgb(5, 50, 255), particleDirection, angleVarianceInDegrees, speedVariance)

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture)

        skyboxProgram = SkyboxShaderProgram(context)
        skybox = SkyBox()
        skyboxTexture = TextureHelper.loadCubeMap(context,
                intArrayOf(R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back))
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f, true)
//        setIdentityM(viewMatrix,0);
//        translateM(viewMatrix,0,0f,-1.5f,-5f)
//        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        drawSkybox()

        //画出粒子
        drawParticles()
    }

    /**
     * 我们以（0,0,0）为中心绘制天空盒，以使我们站在天空盒中间观察，从视觉上看
     */
    private fun drawSkybox() {
        setIdentityM(viewMatrix, 0)
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)//添加移动天空盒子代码
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)//添加移动天空盒子代码
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(viewProjectionMatrix, skyboxTexture)
        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        greenParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        blueParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)

        setIdentityM(viewMatrix, 0)
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleShaderProgram!!.useProgram()
        particleShaderProgram!!.setUniforms(viewProjectionMatrix, currentTime, particleTexture)
        particleSystem!!.bindData(particleShaderProgram!!)
        particleSystem!!.draw()

        glDisable(GL_BLEND)
    }

    fun setGLSurfaceViewTouch(glSurfaceView: GLSurfaceView) {
        glSurfaceView.setOnClickListener(View.OnClickListener {
            glSurfaceView.setOnTouchListener(object : OnTouchListener {
                internal var previousX: Float = 0.toFloat()
                internal var previousY: Float = 0.toFloat()

                override fun onTouch(v: View, event: MotionEvent?): Boolean {
                    if (event != null) {
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            previousX = event.x
                            previousY = event.y
                        } else if (event.action == MotionEvent.ACTION_MOVE) {
                            val deltaX = event.x - previousX
                            val deltaY = event.y - previousY

                            previousX = event.x
                            previousY = event.y

                            glSurfaceView.queueEvent {
                                handleTouchDrag(
                                        deltaX, deltaY)
                            }
                        }

                        return true
                    } else {
                        return false
                    }
                }
            })
        })
    }

}