uniform mat4 u_Matrix;
attribute vec3 a_Position;  
varying vec3 v_Position;

void main()                    
{                                	  	          
    v_Position = a_Position;//首先把顶点位置传递给片段着色器，
    // Make sure to convert from the right-handed coordinate system of the
    // world to the left-handed coordinate system of the cube map, otherwise,
    // our cube map will still work but everything will be flipped.
    //接着在下一行反转其z分量,这个传递给片段着色器的位置就是立方体上每个面之间将被插值的位置，这是我们以后可以使用这个位置查看天空盒的纹理上正确的部分。
    //其z分量被反转了，使得我们可以把试驾你的右手坐标空间转换为天空和所期望的左手坐标空间。如果我们跳过这一步，天空盒忍让能正常工作，但是他的纹理看上去是反的
    v_Position.z = -v_Position.z; 
	           
    gl_Position = u_Matrix * vec4(a_Position, 1.0);
    //通过用a_Position乘以矩阵把那个位置投影到建材空间肘坐标之后，要用下面的代码把其z分量设置为与其w分量相等的值
    //这是一种技巧，他确保天空盒的每一部分豆浆位于归一化设备坐标的圆平面上以及场景中的其他一切后面。
    //这个技巧能够奏效，是因为透视触发把一切都除以w，并且w除以他自己，结果等于1，透视法之后z最终就在值为1的圆平面上了
    gl_Position = gl_Position.xyww;
}    
