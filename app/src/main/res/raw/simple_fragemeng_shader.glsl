#version 120
precision mediump float;

varying vec4 v_Color;
uniform vec4 u_Color; //在这个中存储了4个字节，分别是argb

void main() {
    gl_FragColor=u_Color;
//    gl_FragColor=v_Color;
}
