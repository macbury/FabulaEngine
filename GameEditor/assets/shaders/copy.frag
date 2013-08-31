uniform sampler2D u_texture;
uniform vec2 u_viewport;
uniform sampler2D u_source;
uniform vec2 u_source_size;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(void){
    vec2 uv = gl_FragCoord.xy/u_viewport;
    gl_FragColor = texture2D(u_source, uv);
}