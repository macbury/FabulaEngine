#ifdef GL_ES
precision mediump float;
#endif 

varying vec4  v_color;
varying vec2  v_textCords;

uniform sampler2D u_texture0;

void main() {
  gl_FragColor = v_color * texture2D(u_texture0, v_textCords);
}