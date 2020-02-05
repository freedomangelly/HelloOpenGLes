#version 120
precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;//纹理坐标

void main() 
{
    gl_FragColor=texture2D(u_TextureUnit,v_TextureCoordinates);
}
//为了把纹理会知道一个物体上,OpenGL回味每个片段都调用片段着色器，并且每个调用都接收v_TextureCoordinates的纹理坐标。片段着色器也通过uniform-u_TextureUnit接收实际的纹理数据，u_TextureUnit被定义为一个sampler2D，这个变量类型指的是衣蛾二维纹理数据的数组
//被产值的纹理坐标he纹理数据被传递给着色器texture2d(),他会读入纹理中那个特定坐标处的颜色值。接着通过把结果赋值给gl_FragColor设置片段的颜色