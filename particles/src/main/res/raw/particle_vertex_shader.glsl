#version 120

uniform mat4 u_Matrix;

uniform float u_Time;
attribute vec3 a_Position;
attribute vec3 a_Color;
attribute vec3 a_DirectionVector;
attribute float a_ParticleStartTime;

varying vec3 v_Color;
varying float v_ElapsedTime;
void main() {
    v_Color=a_Color;
    v_ElapsedTime=u_Time - a_ParticleStartTime;
    //通过重力因素的共识瞪大衣蛾加速重力的因子；我么女孩把他除以8以弱化这个效果
    float gravityFactor = v_ElapsedTime * v_ElapsedTime/8.0;
    //我们首先把颜色发送给片段着色器，接着计算例子从被创建之后运行了多长时间，并且把那个时间也发送给片段着色器。为了计算例子的当前位置，我们把方向向量与运行时间相乘，并与a_Position相加。运行时间越长，粒子走的越远。
    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
    //使用重力因子
    currentPosition.y -=gravityFactor;
    gl_Position=u_Matrix * vec4(currentPosition,1.0);
    //我们需要把粒子渲染成一个点，大小设置成10个像素
//    gl_PointSize=10.0;
    gl_PointSize=25.0;
}
