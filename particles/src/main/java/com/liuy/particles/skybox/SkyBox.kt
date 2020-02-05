package com.liuy.particles.skybox

import android.opengl.GLES20.*
import com.liuy.airhockettouch.data.VertexArray
import java.nio.ByteBuffer


/**
 * description:
 * author: freed on 2020/2/5
 * email: 674919909@qq.com
 * version: 1.0
 */
class SkyBox {
    private val POSITION_COMPONENT_COUNT = 3
    private var vertexArray: VertexArray? = null
    private var indexArray: ByteBuffer? = null
    //创建立方体
    constructor() {
        // Create a unit cube.这个数组保存我们的顶点数组，不久我们将使用ByteBuffer保存索引数组。
        vertexArray = VertexArray(floatArrayOf(-1f, 1f, 1f, // (0) Top-left near
                1f, 1f, 1f, // (1) Top-right near
                -1f, -1f, 1f, // (2) Bottom-left near
                1f, -1f, 1f, // (3) Bottom-right near
                -1f, 1f, -1f, // (4) Top-left far
                1f, 1f, -1f, // (5) Top-right far
                -1f, -1f, -1f, // (6) Bottom-left far
                1f, -1f, -1f      // (7) Bottom-right far
        ))

        // 6 indices per cube side
        //索引值，如1代表上面的第1个点，如2就代表上面第3个点
        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(byteArrayOf(
                        // Front
                        1, 3, 0,
                        0, 3, 2,

                        // Back
                        4, 6, 5,
                        5, 6, 7,

                        // Left
                        0, 2, 4,
                        4, 2, 6,

                        // Right
                        5, 7, 1,
                        1, 7, 3,

                        // Top
                        5, 1, 4,
                        4, 1, 0,

                        // Bottom
                        6, 2, 7,
                        7, 2, 3))

        indexArray!!.position(0)
    }
    //bidData()方法是标准的，我们只剩下要定义天空喝的着色器和编译着色器的类还没有完成
    fun bindData(skyboxProgram: SkyboxShaderProgram) {
        vertexArray!!.setVertexAttribPointer(0,
                skyboxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0)
    }
    //要绘制立方体，我么你需要调用用glDrawElements他告诉openGL绘制我们在bindData()中绑定的顶点，他是用了indices所定义的索引数组，并把这个数组解释为无符号字节数，
    fun draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray)
    }
}