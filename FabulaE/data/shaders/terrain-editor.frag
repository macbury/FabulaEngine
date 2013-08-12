#ifdef GL_ES
precision mediump float;
#endif 
varying vec4  v_color;
varying vec2  v_textCords;
varying float v_textureNumber;
varying vec2  v_tile_position;

uniform sampler2D u_texture0;
uniform float u_current_tile_id;
void main() {
  vec4 current_texture = texture2D(u_texture0, v_textCords);
  
  if (false){//(round(v_tileID) == round(u_current_tile_id)) {
    gl_FragColor = vec4(1.0f, 255.0f, 1.0f, 1.0f) * current_texture;
  } else {
    gl_FragColor = current_texture;
  }
  
}