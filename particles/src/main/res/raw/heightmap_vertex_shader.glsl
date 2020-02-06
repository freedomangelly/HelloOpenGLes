#version 120
/*
uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Color;

uniform vec3 u_VecotrToLight;//这个向量将存储指向方向光源的归一化向量

attribute vec3 a_Normal;//用于高度法线的新属性
*/

//眼空间
uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;

uniform vec3 u_VectorToLight;             // In eye space
uniform vec4 u_PointLightPositions[3];    // In eye space
uniform vec3 u_PointLightColors[3];


attribute vec4 a_Position;
attribute vec3 a_Normal;


varying vec3 v_Color;
vec3 materialColor;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;

vec3 getAmbientLighting();
vec3 getDirectionalLighting();
vec3 getPointLighting();

void main() {
    /*
    //这个顶点着色器使用了一个新的着色器还是mix(),用来在两个不同的颜色间做平滑插值。我们配置了高度图，使
    //其高度处于0和1之间，并使用这个额高度作为两个颜色之间的比例。高度图在接近独步的地方呈现绿色，在接近顶部的地方显示灰色
    v_Color=mix(vec3(0.180,0.467,0.153),vec3(0.660,0.670,0.680),a_Position.y);
    gl_Position =u_Matrix * vec4(a_Position,1.0);

    // Note: The lighting code here doesn't take into account any rotations/
    // translations/etc... that may have been done to the model. In that case,
    // the combined model-view matrix should be passed in, and the normals
    // transformed with that matrix (if the matrix contains any skew / scale,
    // then the normals will need to be multiplied by the inverse transpose
    // (see http://arcsynthesis.org/gltut/Illumination/Tut09%20Normal%20Transformation.html
    // for more information)).
    //在绘制高度图的时候，使用scaleM()扩展了它，使他变成10被告和100倍宽，换句话说，高度图现在宽裕他的高度10倍。以这种方式缩放改变了高度图的形状
    //意味着，我们预先产生的法线不正确，为了弥补这一点我们相反的方向缩放法线，使法线高于他的宽度10倍。
    vec3 scaledNormal = a_Normal;
    scaledNormal.y *= 10.0;
    scaledNormal = normalize(scaledNormal);

    //计算朗伯体法线
    //要计算表面与光线之间家教的余弦值，我们要计算指向光源的向量与表面法线的点积。他的工作原理是，当两个向量都是归一化的向量时，那两个向量的点积就是他们之间夹角的预选，这恰恰就是我们计算朗伯体凡是所需要的
    //为了避免出现付的结果，我们用max(把最小预选值限制为0，然后应用这个光线，把当前顶点的颜色与余璇值相乘。余璇值在0和1之间，因此，最终的颜色将是处于黑色和原色之间的某个颜色)
    float diffuse = max(dot(scaledNormal, u_VectorToLight), 0.0);
    diffuse *= 0.3;
    v_Color *= diffuse;

//    float ambient = 0.2;
    float ambient = 0.1;
    v_Color += ambient;
    */

    materialColor = mix(vec3(0.180, 0.467, 0.153),    // A dark green
    vec3(0.660, 0.670, 0.680),    // A stony gray
    a_Position.y);
    eyeSpacePosition = u_MVMatrix * a_Position;

    // The model normals need to be adjusted as per the transpose
    // of the inverse of the modelview matrix.
    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    v_Color = getAmbientLighting();
    v_Color += getDirectionalLighting();
    v_Color += getPointLighting();

    gl_Position = u_MVPMatrix * a_Position;
}

vec3 getAmbientLighting()
{
    return materialColor * 0.1;
}

vec3 getDirectionalLighting()
{
    return materialColor * 0.3
    * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);
}
vec3 getPointLighting()
{
    vec3 lightingSum = vec3(0.0);

    for (int i = 0; i < 3; i++) {
        vec3 toPointLight = vec3(u_PointLightPositions[i])
        - vec3(eyeSpacePosition);
        float distance = length(toPointLight);
        toPointLight = normalize(toPointLight);

        float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
        lightingSum += (materialColor * u_PointLightColors[i] * 5.0 * cosine)
        / distance;
    }

    return lightingSum;
}
