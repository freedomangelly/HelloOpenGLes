package com.liuy.particles.height

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20.*
import android.util.Log
import com.liuy.airhockettouch.util.Consts.BYTES_PER_FLOAT
import com.liuy.airhockettouch.util.Geometry


/**
 * description:
 * author: freed on 2020/2/5
 * email: 674919909@qq.com
 * version: 1.0
 */

class Heightmap {

    private val POSITION_COMPONENT_COUNT = 3
    private val NORMAL_COMPONENT_COUNT = 3
    private val TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT
    private val STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT

    private var width: Int = 0
    private var height: Int = 0
    private var numElements: Int = 0

    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null

    constructor(bitmap: Bitmap){
        width = bitmap.getWidth()
        height = bitmap.getHeight()

        if (width * height > 65536) {
            throw RuntimeException("Heightmap is too large for the index buffer.this buffer size is "+width*height)
        }

        numElements = calculateNumElements()
        vertexBuffer = VertexBuffer(loadBitmapData(bitmap))
        indexBuffer = IndexBuffer(createIndexData())
    }

    /**
     * Copy the heightmap data into a vertex buffer object.
     * 我们把一个Android的Bitmap对象传递进去，把数据加载近一个月顶点缓冲区，并为那些定点创建了一个索引缓冲区。加入loadBitmapData
     */
    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        //为了有效的读位图，我们首先用getPixels索取像素，然后回收bitMap
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()
        //
        val heightmapVertices = FloatArray(width * height * TOTAL_COMPONENT_COUNT)

        var offset = 0
        /**
         * 要生成高度图的每一个顶点，我么首先要计算顶点的位置，高度图在每个方向上都是1个单位宽，且其以x-z平面上的位置（0,0）为中心
         * 因此，通过这些循环，位图的左上角江北映射到（-0.5，-0.5），右下角会被映射到（0.5,0.5）
         * 我们假定这个图像是灰度图，因此，读入其对应响度的红色分量，并把它除以255，得到高度。一个想渎职0对应高度0，二一个像素值255对应高度1,。一旦我们计算完了位置和高度，就可以把这个新的顶点写入数组了
         *
         */
        //把位图的像素，转换为高度图的数据
        /**
         * 让我们仔细的看依稀按这个循环。为什么一行一行地读取这个位图，并从左向右，而且扫描每一行呢，
         * 原因在于其在内存中的布局方式就是这样的，当cpy按顺序缓存和移动数据时，他们更有效率
         *  记住存取像素的方式也很重要。当我们用getPixels()提取像素时，Android给我们返回一个一维数组。那么我么你怎么知道去哪里读入像素呢？
         *  可以用下面的公式计算
         *  像素偏移值=当前行 * 高度 +当前列
         */
        for (row in 0 until height) {
            for (col in 0 until width) {

//                var xPosition:Float=(col.toFloat()/(width-1).toFloat())-0.5f
//                var yPosition:Float=(Color.red(pixels[row *height +col])/255.toFloat()).toFloat()
//                var zPosition:Float=(row.toFloat()/(height-1).toFloat())-0.5f
//
//                heightmapVertices[offset++]=xPosition;
//                heightmapVertices[offset++]=yPosition;
//                heightmapVertices[offset++]=zPosition;


                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.
                val point = getPoint(pixels, row, col)!!

                heightmapVertices[offset++] = point.x
                heightmapVertices[offset++] = point.y
                heightmapVertices[offset++] = point.z
                //让我们再添加写代码，获取当前点的临界点，并为其生成表面法线
                //为了生成这个法线，我们遵循了前面总结的算法：首先获取其临界点，然后，用这些点创建代表其平面的两个向量；最后，采用这两个向量的叉积，并把它归一化以得到其表面法线
                val top = getPoint(pixels, row - 1, col)
                val left = getPoint(pixels, row, col - 1)
                val right = getPoint(pixels, row, col + 1)
                val bottom = getPoint(pixels, row + 1, col)

                val rightToLeft = Geometry.vectorBetween(right, left)
                val topToBottom = Geometry.vectorBetween(top, bottom)
                val normal = rightToLeft.crossProduct(topToBottom).normalize()

                heightmapVertices[offset++] = normal.x
                heightmapVertices[offset++] = normal.y
                heightmapVertices[offset++] = normal.z
            }
        }

        return heightmapVertices
    }

    /**
     * Returns a point at the expected position given by row and col, but if the
     * position is out of bounds, then it clamps the position and uses the
     * clamped position to read the height. For example, calling with row = -1
     * and col = 5 will set the position as if the point really was at -1 and 5,
     * but the height will be set to the heightmap height at (0, 5), since (-1,
     * 5) is out of bounds. This is useful when we're generating normals, and we
     * need to read the heights of neighbouring points.
     */
    private fun getPoint(pixels: IntArray, row: Int, col: Int): Geometry.Point? {
        var row = row
        var col = col
        val x = col.toFloat() / (width - 1).toFloat() - 0.5f
        val z = row.toFloat() / (height - 1).toFloat() - 0.5f
        row = clamp(row, 0, width - 1)
        col = clamp(col, 0, height - 1)
        val y = Color.red(pixels[row * height + col]).toFloat() / 255.toFloat()
        return Geometry.Point(x, y, z)
    }

    private fun clamp(`val`: Int, min: Int, max: Int): Int {
        return Math.max(min, Math.min(max, `val`))
    }

    /**生成索引数据
     * 其工作原理是，针对高度图中每4个额定点构成的组，会生产2个三角形，且每个三角形有3个索引，总共需要6个索引。通过用（width-1）*（height-1）,计算出我们需要多少组
     * 然后我们只需用组*每组两个三角形和每个三角形的3个元素就可以得出所有的元素个数
     * 如3*3图
     * （3-1）*（3-1） = 2*2=4各组，以及每组需要2个三角形和每个三角形需要3个元素，总共24个元素
     */

    private fun calculateNumElements(): Int {
        // There should be 2 triangles for every group of 4 vertices, so a
        // heightmap of, say, 10x10 pixels would have 9x9 groups, with 2
        // triangles per group and 3 vertices per triangle for a total of (9 x 9
        // x 2 x 3) indices.
        return (width - 1) * (height - 1) * 2 * 3
    }

    /**
     * Create an index buffer object for the vertices to wrap them together into
     * triangles, creating indices based on the width and height of the
     * heightmap.
     *这个方法根据需要的大小创建了一个short类型的数组，然后，通过行和列的循环为每4个顶点构成的组创建三角形
     */
    private fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()

                // Write out two triangles.
                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }

        return indexData
    }

    /**
     * 我们使用bindData告诉OpenGL当调用Draw时去哪里获取数据
     */
    fun bindData(heightmapProgram: HeightmapShaderProgram) {
        vertexBuffer!!.setVertexAttribPointer(0,
                heightmapProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE)

        //因为在同一个顶点缓冲区对象中，存储了位置有存储了法线数据，我么你现在不得不把跨距传递给调用glVertexAttribPointer()的辅助函数
        //setVertexAttribPointer,以使OpenGL知道在每个元素之间需要跳过多少字节，第二个setVertexAttribPointer()调用也非常重要，
        //因为我们也为法线制定了以字节为点位的其实偏移值；否则，OpenGL就会读入一部分位置和一部分法线，并把那个值当做法线，这看起来非常怪异
        vertexBuffer!!.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT,
                heightmapProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, STRIDE)
    }

    /**
     * 告诉openggl使用索引缓冲区绘制数据。
     */
    fun draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer!!.bufferId)
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

}