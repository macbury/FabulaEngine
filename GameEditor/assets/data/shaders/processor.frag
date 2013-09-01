uniform sampler2D u_texture;
uniform vec2 u_viewport;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(void){
    vec2 uv = gl_FragCoord.xy/u_viewport;
    gl_FragColor = texture2D(u_texture, uv);
}