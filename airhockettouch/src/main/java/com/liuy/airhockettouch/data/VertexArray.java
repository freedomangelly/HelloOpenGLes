package com.liuy.airhockettouch.data;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.liuy.airhockettouch.util.Consts.BYTES_PER_FLOAT;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer= ByteBuffer
		.allocateDirect(vertexData.length * BYTES_PER_FLOAT)//分配一个本地的内存，GC不会回收塔
                .order(ByteOrder.nativeOrder())//告诉缓冲区，按照native字节序，
                .asFloatBuffer()
                .put(vertexData);

    }

    public void setVertexAttribPointer(int dataOffset,int attributeLocation,
	int componentCount,int stride){
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation,componentCount,
		GL_FLOAT,false,stride,floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }

    public void updateBuffer(@NotNull float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData, start, count);
        floatBuffer.position(0);
    }
}
