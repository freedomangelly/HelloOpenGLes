package com.liuy.airhockettouch.objects;

import com.liuy.airhockettouch.util.Geometry;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * description:
 * author: freed on 2020/1/31
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX=3;//标识一个顶点需要多少浮点
    public static interface DrawCommand{
        void draw();
    }
    
    public static class GeneratedData{
        public float[] vertexData;
        public List<DrawCommand> drawList;

        public GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }

    }


    public static GeneratedData createPuck(Geometry.CyLinder puck, int numPoints){
        //找出需要多少个顶点标识这个冰球
        int size=sizeOfCircleInVertices(numPoints)
                +sizeOfOpenCyLinderInvertices(numPoints);
        //计算冰球顶部应该放在哪里
        ObjectBuilder objectBuilder=new ObjectBuilder(size);
        Geometry.Circle puckTop=new Geometry.Circle(
		puck.center.translateY(puck.height/2f),
		puck.radius);
        objectBuilder.appendCircle(puckTop,numPoints);//创建冰球
        objectBuilder.appendOpenCyLinder(puck,numPoints);//生成冰球侧面
        return objectBuilder.build();
    }

    //计算圆柱体顶部顶点数量的方法
    public static GeneratedData createMallet(
	Geometry.Point center,float radius,float height,int numPoints){
        int size=sizeOfCircleInVertices(numPoints)*2
		+sizeOfOpenCyLinderInvertices(numPoints)*2;
        ObjectBuilder builder=new ObjectBuilder(size);
        // First, generate the mallet base.
        float baseHeight=height*0.25f;
        Geometry.Circle baseCircle=new Geometry.Circle(
		center.translateY(-baseHeight),
		radius);
        Geometry.CyLinder baseCyLinder=new Geometry.CyLinder(
		baseCircle.center.translateY(-baseHeight/2),
		radius,baseHeight);

        builder.appendCircle(baseCircle,numPoints);
        builder.appendOpenCyLinder(baseCyLinder,numPoints);
        // Now generate the mallet handle.
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Geometry.Circle handleCircle=new Geometry.Circle(
		center.translateY(height*0.5f),
		handleRadius);
        Geometry.CyLinder handleCyLinder=new Geometry.CyLinder(
		handleCircle.center.translateY(-handleHeight/2f),
		handleRadius,handleHeight);

        builder.appendCircle(handleCircle,numPoints);
        builder.appendOpenCyLinder(handleCyLinder,numPoints);
        return builder.build();
    }
    private static int sizeOfCircleInVertices(int numPoints){
        return 1+(numPoints+1);
    }
    //计算圆柱体侧面顶点的数量
    private static int sizeOfOpenCyLinderInvertices(int numPoints){
        return (numPoints+1)*2;
    }

	private float[] vertexData;//保存上面的点
	public  List<DrawCommand> drawList=new ArrayList<DrawCommand>();
    private  int offset=0;//记录下一个顶点的位置
    //生成冰球
    public ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }
    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex=offset/FLOATS_PER_VERTEX;
        final int numVertices=sizeOfCircleInVertices(numPoints);
        vertexData[offset++]=circle.center.x;
        vertexData[offset++]=circle.center.y;
        vertexData[offset++]=circle.center.z;

        for(int i=0;i<=numPoints;i++){
            float angleInRadians=
			((float)i/(float)numPoints)
			*((float)Math.PI *2f);
            vertexData[offset++]=
			circle.center.x
                    +circle.radius * (float)Math.cos(angleInRadians);
            vertexData[offset++]=circle.center.y;
            vertexData[offset++]=
			circle.center.z
			+circle.radius*(float)Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN,startVertex,
				numVertices);
            }
        });

    }

    /**
     * 构造冰球的侧面
     * @param puck
     * @param numPoints
     */
    private void appendOpenCyLinder(Geometry.CyLinder puck, int numPoints) {
        final int startVertex=offset/FLOATS_PER_VERTEX;
        final int numVertices=sizeOfOpenCyLinderInvertices(numPoints);
        float yStart=puck.center.y-(puck.height/2f);
        float yEnd=puck.center.y+(puck.height/2f);

        for(int i=0;i<=numPoints;i++){
            float angleInRadians=
                  ((float) i / (float) numPoints)
                * ((float) Math.PI * 2f);
            float xPosition=
			puck.center.x
			+puck.radius* (float) Math.cos(angleInRadians);
            float zPosition=
			puck.center.z
			+puck.radius* (float) Math.sin(angleInRadians);
            vertexData[offset++]=xPosition;
            vertexData[offset++]=yStart;
            vertexData[offset++]=zPosition;
            vertexData[offset++]=xPosition;
            vertexData[offset++]=yEnd;
            vertexData[offset++]=zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex,
                    numVertices);
            }
        });
    }

    private GeneratedData build(){
        return new GeneratedData(vertexData,drawList);
    }



}
