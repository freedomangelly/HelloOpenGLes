package com.liuy.particles.height

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.liuy.airhockettouch.util.Geometry
import com.liuy.airhockettouch.util.MatrixHelper
import com.liuy.airhockettouch.util.TextureHelper
import com.liuy.particles.ParticleShaderProgram
import com.liuy.particles.ParticleShooter
import com.liuy.particles.ParticleSystem
import com.liuy.particles.R
import com.liuy.particles.skybox.Skybox
import com.liuy.particles.skybox.SkyboxShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


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
    private var skybox: Skybox? = null
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

    //光照
    /**
     * 这个向量大约指向天空盒的太阳。你可以用下面这些步骤得出一个相似的结果
     * 1.创建一个指向（0,0，-1）的向量，也就是正上方。
     * 2.按场景旋转方向的反向旋转者向量
     * 3.加入日志，打印这个额向量当前的方向，然后调试直到太阳处于屏幕中心
     */

//    var vectorToLight:Geometry.Vector = Geometry.Vector(0.61f,0.64f,-0.47f).normalize()
//    var vectorToLight:Geometry.Vector = Geometry.Vector(0.30f,0.35f,-0.89f).normalize()

    //眼世界

    private val modelViewMatrix = FloatArray(16)
    private val it_modelViewMatrix = FloatArray(16)

    //我们也要把每个点光源的位置和颜色存到他们各自的数组中，这些位置和颜色与我们为每个离子发生器设定的位置和颜色大致匹配。其主要的区别在于每个点光宇都被放在他的粒子放射器设定的位置和颜色大致匹配
    //其主要的区别在于每个点光源都被放在他的粒子发射器上方一个单位处。因为地形是绿色的，绿色的光也稍微变暗些，这样他就不会压制住红光和蓝光
    val vectorToLight = floatArrayOf(0.30f, 0.35f, -0.89f, 0f)

    private val pointLightPositions = floatArrayOf(-1f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f)

    private val pointLightColors = floatArrayOf(1.00f, 0.20f, 0.02f,
            0.02f, 0.25f, 0.02f,
            0.02f, 0.20f, 1.00f)

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
        skybox = Skybox()
//        skyboxTexture = TextureHelper.loadCubeMap(context,
//                intArrayOf(R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back))
        skyboxTexture = TextureHelper.loadCubeMap(context, intArrayOf(R.drawable.night_left, R.drawable.night_right,
                R.drawable.night_bottom, R.drawable.night_top,
                R.drawable.night_front, R.drawable.night_back))
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
        //眼世界
        // Put the light positions into eye space.
        // 我们需要把方向光的向量和点光源的位置放入眼空间中，为此，我么使用Android的Matrix类吧他么乘以试图矩阵。那些位置已经在世界空间中了，因此，不必实现把他们与模型矩阵相乘
        val vectorToLightInEyeSpace = FloatArray(4)
        val pointPositionsInEyeSpace = FloatArray(12)
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0)
        multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0)
        multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4)
        multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8)

        heightmapProgram!!.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, vectorToLightInEyeSpace,
                pointPositionsInEyeSpace, pointLightColors)
        //~眼世界
//        heightmapProgram!!.setUniforms(modelViewProjectionMatrix,vectorToLight)
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
        //把modelViewMatrix设置为合并后的模型视图矩阵，
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        //把it_modelViewMatrix设置为那个反转矩阵的倒置
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
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