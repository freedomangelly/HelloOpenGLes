package com.liuy.particles

import com.liuy.airhockettouch.util.Geometry
import java.util.*
import android.opengl.Matrix.multiplyMV
import android.opengl.Matrix.setRotateEulerM



/**
 * description:
 * author: freed on 2020/2/4
 * email: 674919909@qq.com
 * version: 1.0
 * 每个发射器豆浆有一个角度变化量，用来控制粒子的扩散，以及一个速度变化量，用来改变每个粒子的速度。我么你也有一个矩阵和两个向量，因此，我们可以使用android的Matrix类做运算
 */
class ParticleShooter {
    val position: Geometry.Point
    val color: Int
    val direction: Geometry.Vector


    val angleVariance:Float
    val speedVariance:Float
    val random:Random = Random()
    var rotationMatrix:FloatArray=FloatArray(16)
    var directionVector:FloatArray=FloatArray(4)
    var resultVector:FloatArray=FloatArray(4)

    constructor(position: Geometry.Point,
                color: Int,
                direction: Geometry.Vector,angleVarianceInDegrees:Float,speedVariance:Float){
        this.position=position
        this.color=color
        this.direction=direction

        this.angleVariance=angleVarianceInDegrees
        this.speedVariance=speedVariance

        directionVector[0]=direction.x
        directionVector[1]=direction.y
        directionVector[2]=direction.z
    }
    //更新粒子的角度和速度的变化量
    open fun addParticles(particleSystem: ParticleSystem,currentTime:Float,count:Int){
        for (i in 0..count){
                //此方法为单一粒子
            //            particleSystem.addParticle(position,color,direction,currentTime)

            //随机改变发射角度，其单位是度
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance)
            //接下来我们把这个矩阵预防先相连相乘，得到一个角度稍小的旋转向量

            multiplyMV(
                    resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0)
            //要调整速度，我们把方向向量的每一个分量都与相同的speedVariance的随机调整值相乘
            val speedAdjustment = 1f + random.nextFloat() * speedVariance

            val thisDirection = Geometry.Vector(
                    resultVector[0] * speedAdjustment,
                    resultVector[1] * speedAdjustment,
                    resultVector[2] * speedAdjustment)

            particleSystem.addParticle(position,color,thisDirection,currentTime)
        }
    }
}