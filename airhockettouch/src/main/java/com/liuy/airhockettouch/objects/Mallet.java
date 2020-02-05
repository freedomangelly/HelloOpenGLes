package com.liuy.airhockettouch.objects;

import com.liuy.airhockettouch.data.VertexArray;
import com.liuy.airhockettouch.programs.ColorShaderProgram;
import com.liuy.airhockettouch.util.Geometry;

import java.util.List;

/**
 * description:
 * author: freed on 2020/1/30
 * email: 674919909@qq.com
 * version: 1.0
 */
public class Mallet {
//    private static final int POSITION_COMPONENT_COUNT=2;
//    private static final int COLOR_COMPONENT_COUNT=3;
//    private static final int STRIDE=(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)* Consts.BYTES_PER_FLOAT;
//    private static final float[] VERTEX_DATA={
//            //x y r g b
//            0f,-0.4f,0f,0f,1f,
//            0f,0.4f,1f,0f,0f
//    };
//    private final VertexArray vertexArray;
//
//    public Mallet() {
//        this.vertexArray = new VertexArray(VERTEX_DATA);
//    }
//
//    public void draw(){
//        glDrawArrays(GL_POINTS,0,2);
//    }
//
//    public void bindData(ColorShaderProgram textureShaderProgram){
//        vertexArray.setVertexAttribPointer(0,
//                textureShaderProgram.getPositionAttributeLocation(),
//                POSITION_COMPONENT_COUNT,STRIDE);
//        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,textureShaderProgram.getColorAttributeLocation(),
//                COLOR_COMPONENT_COUNT,STRIDE);
//    }

    private static final int POSITION_COMPONENT_COUNT=3;
    public  float radius;
    public  float height;

    private VertexArray vertexArray;
    private List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height,int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData=ObjectBuilder.createMallet(new Geometry.Point(0f,
		0f,0f),radius,height,numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray=new VertexArray(generatedData.vertexData);
        drawList=generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,
		colorShaderProgram.getPositionAttributeLocation(),
		POSITION_COMPONENT_COUNT,0);
    }
    public void draw(){
        for (ObjectBuilder.DrawCommand drawCommand : drawList){
            drawCommand.draw();
        }
    }
}
