#ifdef GL_ES
precision mediump float;
#endif 
varying vec4 v_color;
varying vec2 v_textCords;
varying float v_textureNumber;

uniform sampler2D u_texture0;
void main() {
  vec4 current_texture = texture2D(u_texture0, v_textCords);
  
  //if (v_textureNumber == 0.0f) {
  gl_FragColor = v_color * current_texture;
  ////} else {
    //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0) * texture2D(u_texture, v_textCords);
  //}
  
}