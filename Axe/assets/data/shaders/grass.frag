#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D   u_texture;
varying vec2        v_textCords;
void main () {
  gl_FragColor = texture2D(u_texture, v_textCords);
}