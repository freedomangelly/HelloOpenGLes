#version 120

attribute vec4 a_Position;//vec4标识xyzw四个坐标,
//attribute vec4 a_Color;
//attribute 中包含了，颜色位置等属性
//着色器的主入口、所做的就是把前面定义过的位置复制到指定的输出变量gl_Position中
varying vec4 v_Color;
void main() {
//    v_Color=a_Color;

    gl_Position = a_Position;
    gl_PointSize=10.0;
}
