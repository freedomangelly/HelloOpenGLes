package com.liuy.particles.data

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
import com.liuy.particles.ParticleShaderProgram
import com.liuy.particles.ParticleShooter
import com.liuy.particles.ParticleSystem
import com.liuy.particles.skybox.SkyBox
import com.liuy.particles.skybox.SkyboxShaderProgram
import android.graphics.drawable.BitmapDrawable
import com.liuy.particles.R


/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 */

class HeightmapRenderer : GLSurfaceView.Renderer {

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

    //增加地形
    private val modelMatrix = FloatArray(16)
    private val viewMatrixForSkybox = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private var heightmapProgram: HeightmapShaderProgram? = null
    private var heightmap: Heightmap? = null

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
        updateViewMatrices()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清屏为黑色
        glClearColor(0f, 0f, 0f, 0f)
        glEnable(GL_DEPTH_TEST);//打开深度缓冲区功能
        glEnable(GL_CULL_FACE);
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
        //天空盒子
        skyboxProgram = SkyboxShaderProgram(context)
        skybox = SkyBox()
        skyboxTexture = TextureHelper.loadCubeMap(context,
                intArrayOf(R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back))
        //增加地形
        heightmapProgram = HeightmapShaderProgram(context)
        heightmap = Heightmap((context.resources
                .getDrawable(R.drawable.heightmap) as BitmapDrawable).bitmap)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f, true)

        //添加高低
        updateViewMatrices();
    }

    override fun onDrawFrame(gl: GL10?) {
//        glClear(GL_COLOR_BUFFER_BIT)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)//告诉openGL在每个新帧上也要清空深度缓冲区
        drawHeightmap()
        drawSkybox()
//
//        //画出粒子
        drawParticles()
    }

    /**
     * 绘制地形图
     * 因为我们不想让这个山太突兀，我们用模型矩阵是高度图在x和z方向上变宽100倍，而在y方向上只变高10倍。
     * 等等，我们知道着色器中的额颜色插值依赖于顶点所在位置的y值，这不会扰乱他们吗？不会的因为罩色漆读入顶点位置的时间是我们把它与矩阵相乘时间
     */

    private fun drawHeightmap() {
        setIdentityM(modelMatrix, 0)
        // Expand the heightmap's dimensions, but don't expand the height as
        // much so that we don't get insanely tall mountains.
        scaleM(modelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrix()
        heightmapProgram!!.useProgram()
        heightmapProgram!!.setUniforms(modelViewProjectionMatrix)
        heightmap!!.bindData(heightmapProgram!!)
        heightmap!!.draw()
    }

    /**
     * 我们以（0,0,0）为中心绘制天空盒，以使我们站在天空盒中间观察，从视觉上看
     */
    private fun drawSkybox() {
        setIdentityM(modelMatrix, 0)
        updateMvpMatrixForSkybox()

        glDepthFunc(GL_LEQUAL) // This avoids problems with the skybox itself getting clipped.
        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(modelViewProjectionMatrix, skyboxTexture)
        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()
        glDepthFunc(GL_LESS)//深度设置为GL_LESS,意味着：如果新片段比任何已经存在哪里的片段进或者比远平面近，就让他通过测试
        //GL_LEQUAL如果新片段与已经存在那里的片段相比较劲或者二者在同等距离处就让他们通过测试
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        greenParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        blueParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)

        setIdentityM(modelMatrix, 0)
        updateMvpMatrix()

        glDepthMask(false)
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)

        particleShaderProgram!!.useProgram()
        particleShaderProgram!!.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture)
        particleSystem!!.bindData(particleShaderProgram!!)
        particleSystem!!.draw()

        glDisable(GL_BLEND)
        glDepthMask(true)
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

    /**
     * 为了减少矩阵代码复制和粘贴的使用量，需要做多次改动，为此，我们使用了一个㡯代表相继，它应用于所有的物体，再为天空盒定义一个矩阵，他只表示旋转
     * 通过这段代码，我么您就可以使用viewMatrix一起旋转和平移高度图和粒子了，我们也可以使用viewMatrixForSkybox旋转天空盒
     */
    private fun updateViewMatrices() {
        setIdentityM(viewMatrix, 0)
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.size)

        // We want the translation to apply to the regular view matrix, and not
        // the skybox.
        translateM(viewMatrix, 0, 0f, -1.5f, -5f)
    }

    /**
     * 绘制其他物体，把这些矩阵相乘形成一个最终合并在一起的模型-视图投影矩阵
     */
    private fun updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }

    /**
     * 绘制天空盒，把这些矩阵相乘形成一个最终合并在一起的模型-视图投影矩阵
     */
    private fun updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0)
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }

}