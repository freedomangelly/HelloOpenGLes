package com.liuy.helloopengles;

import android.content.Context;
import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.liuy.helloopengles.objects.Mallet;
import com.liuy.helloopengles.objects.Puck;
import com.liuy.helloopengles.objects.Table;
import com.liuy.helloopengles.programs.ColorShaderProgram;
import com.liuy.helloopengles.programs.TextureShaderProgram;
import com.liuy.helloopengles.util.Geometry;
import com.liuy.helloopengles.util.MatrixHelper;
import com.liuy.helloopengles.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class ColorRender3 implements GLSurfaceView.Renderer {
    private Context mContext;
    private float[] projectionMatrix=new float[16];
    private float[] modelMatrix=new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    private float[] viewMatrix=new float[16];
    private float[] viewProjectionMatrix=new float[16];
    private float[] modelViewProjectionMatrix=new float[16];
    private Puck puck;


    private boolean malletPressed=false;
    private Geometry.Point blueMalletPosition;

    /**
     * 构造函数
     * @param context
     */
    public ColorRender3(Context context) {
        this.mContext=context;
    }
    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray=converNormalized2DPointToRay(normalizedX,normalizedY);
        Geometry.Sphere malletBounding=new Geometry.Sphere(new Geometry.Point(
		blueMalletPosition.x,
		blueMalletPosition.y,
		blueMalletPosition.z),
		mallet.height/2f);
        malletPressed=Geometry.intersects(malletBounding,ray);
    }
    private Geometry.Ray converNormalized2DPointToRay(
	float normalizedX, float normalizedY) {
        float[] nearPointNdc={normalizedX,normalizedY,-1,1};
        float[] farPointNdc={normalizedX,normalizedY,1,1};
        float[] nearPointWorld=new float[4];
        float[] farPointWorld=new float[4];
        multiplyMV(
		nearPointWorld,0,invertedViewProjectionMatrix,0,nearPointNdc,0);
        multiplyMV(
		farPointWorld,0,invertedViewProjectionMatrix,0,farPointNdc,0);
        divedeByW(nearPointWorld);
        divedeByW(farPointWorld);
        Geometry.Point nearPointRay=
		new Geometry.Point(nearPointWorld[0],nearPointWorld[1],nearPointWorld[2]);
        Geometry.Point farPointRay=
		new Geometry.Point(farPointWorld[0],farPointWorld[1],farPointWorld[2]);
        return new Geometry.Ray(nearPointRay,
		Geometry.vectorBetween(nearPointRay,farPointRay));
    }

    private void divedeByW(float[] vector) {
        vector[0]/=vector[3];
        vector[1]/=vector[3];
        vector[2]/=vector[3];
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if(malletPressed){
            Geometry.Ray ray = converNormalized2DPointToRay(normalizedX,normalizedY);
            Geometry.Plane plne=new Geometry.Plane(new Geometry.Point(0,0,0),new Geometry.Vector(0,1,0));
            Geometry.Point touchedPoint=Geometry.intersectionPoint(ray,plne);
//            blueMalletPosition=new Geometry.Point(touchedPoint.x,mallet.height/2f,touchedPoint.z);
//            blueMalletPosition=new Geometry.Point(clamp(touchedPoint.x,leftBound+mallet.radius,rightBound+mallet.radius),mallet.height/2f,clamp(touchedPoint.z,0f+mallet.radius,nearBound-mallet.radius));
            previousBlueMalletPosition=blueMalletPosition;
            blueMalletPosition = new Geometry.Point(
                clamp(touchedPoint.x, 
                      leftBound + mallet.radius, 
                      rightBound - mallet.radius),
                mallet.height / 2f, 
                clamp(touchedPoint.z, 
                      0f + mallet.radius, 
                      nearBound - mallet.radius));            
            float distance=Geometry.vectorBetween(blueMalletPosition,puckPosition).length();
            if(distance<(puck.radius + mallet.radius)){
                puckVector =Geometry.vectorBetween(previousBlueMalletPosition,blueMalletPosition);
            }
        }
    }
    private float clamp(float value,float min,float max){
        return Math.min(max,Math.max(value,min));
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {//创建的时候被调用，但是也可能显示多次
        glClearColor(0f,0f,0f,0f);
        table=new Table();
        mallet=new Mallet(0.08f,0.15f,32);
        puck=new Puck(0.06f,0.02f,32);
		blueMalletPosition=new Geometry.Point(0f,mallet.height/2f,0.4f);

        puckPosition=new Geometry.Point(0f,puck.height/2f,0f);
        puckVector=new Geometry.Vector(0f,0f,0f);
        textureShaderProgram=new TextureShaderProgram(mContext);
        colorShaderProgram=new ColorShaderProgram(mContext);

        texture= TextureHelper.loadTexture(mContext,R.drawable.air_hockey_surface);

        
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {//大小改变的时候被调用
        GLES30.glViewport(0,0,width,height);//设置窗口尺寸
        MatrixHelper.perspectiveM(projectionMatrix,45,(float)width/(float)height,1f,10f,true);
        Matrix.setLookAtM(viewMatrix,0,0f,1.2f,2.2f,0f,0f,0f,0f,1f,0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {//绘制第一帧的是时候被调用
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);//表示清空屏幕，这会擦除屏幕上的所有演示，用之前glClearColor定义的颜色填充整个屏幕
        puckPosition=puckPosition.translate(puckVector);

        // If the puck struck a side, reflect it off that side.
        if (puckPosition.x < leftBound + puck.radius
                || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius
                || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        // Clamp the puck position.
        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

        // Friction factor
        puckVector = puckVector.scale(0.99f);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
                viewMatrix, 0);
        invertM(invertedViewProjectionMatrix,0,viewProjectionMatrix,0);
        // Draw the mallets.
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewProjectionMatrix,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        positionObjectInScene(0f,mallet.height/2f,-0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,1f,0f,0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x,blueMalletPosition.y,blueMalletPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,0f,0f,1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x,puckPosition.y,puckPosition.z);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix,0.8f,0.8f,1f);
        puck.bindData(colorShaderProgram);
        puck.draw();

        

    }
    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    // The mallets and the puck are positioned on the same plane as the table.
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }







    private float[] invertedViewProjectionMatrix=new float[16];
    private float leftBound=-0.5f;
    private float rightBound=0.5f;
    private float farBound=-0.8f;
    private float nearBound=0.8f;
    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;


}
