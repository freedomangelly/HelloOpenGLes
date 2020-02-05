package com.liuy.airhockettouch.objects;


import com.liuy.airhockettouch.data.VertexArray;
import com.liuy.airhockettouch.programs.ColorShaderProgram;
import com.liuy.airhockettouch.util.Geometry;

import java.util.List;

/**
 * description:
 * author: freed on 2020/1/31
 * email: 674919909@qq.com
 * version: 1.0
 */
public class Puck {
    private static int POSITION_COMPONENT_COUNT=3;

    public float radius,height;

    private VertexArray vertexArray;
    private List<ObjectBuilder.DrawCommand> drwaList;

    public Puck(float radius, float height,int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData=ObjectBuilder.createPuck(new Geometry.CyLinder(
		new Geometry.Point(0f,0f,0f),radius,height),numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray=new VertexArray(generatedData.vertexData);
        drwaList=generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorShaderProgram){
        vertexArray.setVertexAttribPointer(0,
		colorShaderProgram.getPositionAttributeLocation(),
		POSITION_COMPONENT_COUNT,0);
    }
    public void draw(){
        for (ObjectBuilder.DrawCommand drawCommand:drwaList){
            drawCommand.draw();
        }
    }
}
