package com.liuy.particles

import android.graphics.Color
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.liuy.airhockettouch.data.VertexArray
import com.liuy.airhockettouch.util.Consts
import com.liuy.airhockettouch.util.Geometry

/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 * 构建粒子系统
 */

open class ParticleSystem {
    companion object{
        private val POSITION_COMPONENT_COUNT=3
        private val COLOR_COMPONENT_COUNT=3
        private val VECTOR_COMPONENT_COUNT=3
        private val PARTICLE_START_TIME_COMPONENT_COUNT=1

        private val TOTAL_COMPONENT_COUNT= POSITION_COMPONENT_COUNT+ COLOR_COMPONENT_COUNT+ VECTOR_COMPONENT_COUNT+ PARTICLE_START_TIME_COMPONENT_COUNT

        private val STRIDE= TOTAL_COMPONENT_COUNT*Consts.BYTES_PER_FLOAT
    }

    private val particles:FloatArray//存储粒子的数组
    private val vertexArray:VertexArray//发送opengl的数据
    private val maxParticleCount:Int //最大粒子个数
    private var currentParticleCount:Int=0;//当前粒子数
    private var nextParticle:Int=0//数组中的index

    constructor(maxParticleCount:Int){
        particles= FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
        vertexArray= VertexArray(particles)
        this.maxParticleCount=maxParticleCount
    }

    /**
     * 添加粒子
     */
    open fun  addParticle(postion: Geometry.Point,color:Int,direction:Geometry.Vector ,particleStartTime:Float){
        val particleOffSet=nextParticle* TOTAL_COMPONENT_COUNT

        var currentOffSet:Int=particleOffSet
        nextParticle++

        if(currentParticleCount<maxParticleCount){
            currentParticleCount++
        }
        if(nextParticle == maxParticleCount){
            nextParticle = 0;
        }

        //粒子的位置
        particles[currentOffSet++]=postion.x
        particles[currentOffSet++]=postion.y
        particles[currentOffSet++]=postion.z
        //粒子的颜色
        particles[currentOffSet++]=Color.red(color)/255f
        particles[currentOffSet++]=Color.green(color)/255f
        particles[currentOffSet++]=Color.blue(color)/255f
        //粒子的方向
        particles[currentOffSet++]=direction.x
        particles[currentOffSet++]=direction.y
        particles[currentOffSet++]=direction.z
        //粒子的创建时间
        particles[currentOffSet++]=particleStartTime
        //把粒子复制到本地缓冲区,我们只是想复制这些数据，这样就不会浪费时间去复制那些没有改变的数据
        vertexArray.updateBuffer(particles,particleOffSet, TOTAL_COMPONENT_COUNT)
    }

    open fun bindData(particleShaderProgram: ParticleShaderProgram){
        var dataOffset=0
        vertexArray.setVertexAttribPointer(dataOffset,particleShaderProgram.getPoisitionAttributteLocation(),POSITION_COMPONENT_COUNT, STRIDE)
        dataOffset+= POSITION_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,particleShaderProgram.getColorAttributeLocation(), COLOR_COMPONENT_COUNT, STRIDE)
        dataOffset+= COLOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,particleShaderProgram.getDirectionVectorAttributeLocation(), VECTOR_COMPONENT_COUNT, STRIDE)
        dataOffset+= VECTOR_COMPONENT_COUNT
        vertexArray.setVertexAttribPointer(dataOffset,particleShaderProgram.getParticleStartTimeAttributeLocation(), PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE)
    }

    open fun draw(){
        glDrawArrays(GL_POINTS,0,currentParticleCount)
    }


}