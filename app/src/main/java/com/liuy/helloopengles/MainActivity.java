package com.liuy.helloopengles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.liuy.airhockettouch.AirHockeyRenderer;
import com.liuy.particles.ParticlesRenderer;
import com.liuy.particles.data.HeightmapRenderer;
import com.liuy.particles.skybox.SkyRenderer;


public class MainActivity extends AppCompatActivity {
    private String TAG = "testOpengles";
    private GLSurfaceView mGLSurfaceView;

    // Used to load the 'native-lib' library on application startup.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        isSupportsEs2();
        setupView();

    }

    private void setupView() {
        mGLSurfaceView =new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);//设置opengl的版本
        final HeightmapRenderer renderer=new HeightmapRenderer(this);
        mGLSurfaceView.setRenderer(renderer);
        renderer.setGLSurfaceViewTouch(mGLSurfaceView);
    }

    //设备是否支持opengl es 2.0
    private boolean isSupportsEs2(){
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        Log.i(TAG,"version="+configurationInfo.reqGlEsVersion+","+(configurationInfo.reqGlEsVersion>=0x2));
        return configurationInfo.reqGlEsVersion>=0x2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGLSurfaceView!=null){//这么做可以防止程序崩溃
            mGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGLSurfaceView!=null){//这么做可以防止程序崩溃
            mGLSurfaceView.onPause();
        }
    }
}
