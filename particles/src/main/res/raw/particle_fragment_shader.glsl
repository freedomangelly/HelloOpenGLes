#version 120

precision mediump float;
varying vec3 v_Color;
varying float v_ElapsedTime;

uniform sampler2D u_TextureUnit;
void main() {
    //通过把颜色除以运行时间，这个着色器会使年轻的例子明亮，而使老年的例子暗淡，那么除以0怎么办？，根据规范，这会导致一个不明确的结果，但不会导致程序崩溃，你可以在分母上加一个很小的数
//    gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0);

    //用这种方法把一个点绘制为圆，其开销有点大，但是它能工作。他的工作原理是：每个片段，我么能使用勾股定理计算器与圆心的距离，如果距离大于0.5单位的半径，那么当前的片段就不是圆的一部分，我们还是用了特殊发的关键词 “discard”告诉opengl丢掉这个片段
//    float xDistance = 0.5 - gl_PointCoord.x;
//    float yDistance = 0.5 - gl_PointCoord.y;
//    float distanceFromCenter =
//    sqrt(xDistance * xDistance + yDistance * yDistance);
//    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
//
//    if (distanceFromCenter > 0.5) {
//        discard;
//    } else {
//        gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
//    }

    //他会使用gl_PointCoore作为纹理坐标在每个电商绘制一个纹理。纹理的颜色会与点的颜色相乘，这样就可以与以前一样的方式染上颜色
    gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0)
    * texture2D(u_TextureUnit, gl_PointCoord);
}
