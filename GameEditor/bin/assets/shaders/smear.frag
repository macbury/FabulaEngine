uniform sampler2D u_texture;
uniform vec2 u_viewport;
uniform int u_sample_count;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(void){
    vec2 uv = gl_FragCoord.xy/u_viewport;
    vec4 col = texture2D(u_texture, uv);
    vec4 upper = vec4(0.0, 0.0, 0.0, 0.0);
    float iter = 0.0;
    for (int i=-u_sample_count/2; i < u_sample_count/2+1; i++) {
    	vec2 fragtmp = gl_FragCoord.xy;
    	fragtmp.x += i;
    	fragtmp.y += float(u_sample_count)/12.0;
    	vec2 uvtmp = fragtmp/u_viewport;
    	vec4 colup = texture2D(u_texture, uvtmp);
    	upper.r += colup.r;
    	upper.g += colup.g;
    	upper.b += colup.b;
    	upper.a += colup.a;
    	iter += 1;
    }
    upper /= iter;
    gl_FragColor = mix(upper, col, 1-upper.a);
    //gl_FragColor = (col + colup) * 0.5;
}