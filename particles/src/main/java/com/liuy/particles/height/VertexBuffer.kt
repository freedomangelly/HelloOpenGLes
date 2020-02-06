package com.liuy.particles.height

import android.opengl.GLES20.*
import com.liuy.airhockettouch.util.Consts
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * description:
 * author: freed on 2020/2/5
 * email: 674919909@qq.com
 * version: 1.0
 */

class VertexBuffer {
    var bufferId:Int=0

    constructor(vertexData:FloatArray){
        //Allocate a buffer
        val buffers:IntArray= IntArray(1);
        glGenBuffers(buffers.size,buffers,0)//创建一个新的缓冲区
        if(buffers[0]==0){
            throw RuntimeException("could not create a new vertex buffer object.")
        }
        bufferId=buffers[0]
        //bind to the buffer
        glBindBuffer(GL_ARRAY_BUFFER,buffers[0])//绑定这个缓冲区

        //Transfer data to native memory//创建要复制缓冲区的数组
        var vertexArray:FloatBuffer = ByteBuffer.
                allocateDirect(vertexData.size * Consts.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData)
        vertexArray.position(0)
        //Transfer data from native memory to the GPU buffer
        /**int target,顶点缓冲区对象，应该设GL_ARRAY_BUFFER,索引缓冲区对象应该设GL_ELEMENT_ARRAY_BUFFER
        int size,   数据的大小（以字节为单位）
        java.nio.Buffer data,缓冲区数据
        int usage 告诉缓冲区对象所期望的使用模式选项有
            GL_STREAM_DRAW这个对象只会被修改一次，并且不会被经常使用
            GL_STATIC_DRAW这个对象将被修改一次，但是会经常使用
            GL_DYNAMIC_DRAW 这个对此昂江北修改和使用很多次
            这些知识提示，而不是限制，所以OpenGL可以根据需要做任何优化。大多数情况下，我们都是用GL_STATIC_DRAW
         *
         */
        glBufferData(GL_ARRAY_BUFFER,vertexArray.capacity() * Consts.BYTES_PER_FLOAT,vertexArray, GL_STATIC_DRAW)//调用把数据传到缓冲区对象
        //IMPORTANT:Unbind from the buffer when we're done with it.
        glBindBuffer(GL_ARRAY_BUFFER,0)
    }

    fun setVertexAttribPointer(dataOffset:Int,attributeLocation:Int,componentCount:Int,stride:Int){
        glBindBuffer(GL_ARRAY_BUFFER,bufferId)//需要绑定到缓冲区
        // 使用了一个稍微不同的glVertexAttribPointer,他的最后一个参数为int，这个整数告诉OpenGL，当前属性对应的一字节为单位的偏移值；对于第一个属性，它可能是0，对于其后的属性，他就是一个指定的字节偏移值
        glVertexAttribPointer(attributeLocation,componentCount,GL_FLOAT,false,stride,dataOffset)
        glEnableVertexAttribArray(attributeLocation);
        //确保与缓冲区解除绑定
        glBindBuffer(GL_ARRAY_BUFFER,0)
    }

}
