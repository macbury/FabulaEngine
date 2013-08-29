uniform sampler2D u_texture;
uniform vec2 u_viewport, u_axis;
uniform float u_radius;

float weights3[3] = float[3](0.3125, 0.375, 0.3125);
float weights5[5] = float[5](0.0822, 0.2351, 0.3654, 0.2351, 0.0822);
float weights7[7] = float[7](0.0242, 0.091, 0.2206, 0.3284, 0.2206, 0.091, 0.0242);
float weights9[9] = float[9](0.0162162162, 0.0540540541, 0.1216216216, 0.1945945946, 0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162); 

vec4 calcSum3() {
    vec2 off = u_axis/u_viewport;
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 sum = vec4(0.0);
    
    for (int i=0; i < weights3.length(); i++) {
        vec2 offset = off * (i - u_radius/2.0);
        sum += texture2D(u_texture, uv+offset) * weights3[i];
    }
    return sum;
}

vec4 calcSum5() {
    vec2 off = u_axis/u_viewport;
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 sum = vec4(0.0);
    
    for (int i=0; i < weights5.length(); i++) {
        vec2 offset = off * (i - u_radius/2.0);
        sum += texture2D(u_texture, uv+offset) * weights5[i];
    }
    return sum;
}

vec4 calcSum7() {
    vec2 off = u_axis/u_viewport;
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 sum = vec4(0.0);
    
    for (int i=0; i < weights7.length(); i++) {
        vec2 offset = off * (i - u_radius/2.0);
        sum += texture2D(u_texture, uv+offset) * weights7[i];
    }
    return sum;
}

vec4 calcSum9() {
    vec2 off = u_axis/u_viewport;
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 sum = vec4(0.0);
    
    for (int i=0; i < weights9.length(); i++) {
        vec2 offset = off * (i - u_radius/2.0);
        sum += texture2D(u_texture, uv+offset) * weights9[i];
    }
    return sum;
}

void main(void){
    vec4 sum = vec4(0.0);
    if (u_radius == 3)
        sum = calcSum3();
    else if (u_radius == 5)
        sum = calcSum5();
    else if (u_radius == 7)
        sum = calcSum7();
    else if (u_radius == 9)
        sum = calcSum9();
    
    gl_FragColor = sum;
}
