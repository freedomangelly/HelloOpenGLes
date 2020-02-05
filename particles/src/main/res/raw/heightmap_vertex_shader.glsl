#version 120

uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Color;


void main() {
    //这个顶点着色器使用了一个新的着色器还是mix(),用来在两个不同的颜色间做平滑插值。我们配置了高度图，使
    //其高度处于0和1之间，并使用这个额高度作为两个颜色之间的比例。高度图在接近独步的地方呈现绿色，在接近顶部的地方显示灰色
    v_Color=mix(vec3(0.180,0.467,0.153),vec3(0.660,0.670,0.680),a_Position.y);
    gl_Position =u_Matrix * vec4(a_Position,1.0);
}
