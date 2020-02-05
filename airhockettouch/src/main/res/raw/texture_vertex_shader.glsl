#version 120

uniform mat4 u_Matrix;

attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;//他有两个坐标，S和T，所以被定义为vec2,

varying vec2 v_TextureCoordinates; //顶点着色器被插值

void main()
{
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
}
