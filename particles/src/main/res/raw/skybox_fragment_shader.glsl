precision mediump float; 

uniform samplerCube u_TextureUnit;
varying vec3 v_Position;
	    	   								
void main()                    		
{
	gl_FragColor = textureCube(u_TextureUnit, v_Position);    
}
//为了使用个立方体当纹理绘制这个天空和我们调用了textureCube（）时，把那个被插值的立方体面的位置作为内阁片段的纹理坐标。
