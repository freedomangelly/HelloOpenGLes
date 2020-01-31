#version 120
precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;//纹理坐标

void main() 
{
    gl_FragColor=texture2D(u_TextureUnit,v_TextureCoordinates);
}
