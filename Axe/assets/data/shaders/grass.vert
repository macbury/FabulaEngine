#ifdef GL_ES
precision highp float;
#endif

attribute vec4 a_position;
attribute vec2 a_textCords;

varying vec2   v_textCords;

uniform mat4   u_model_view;

void main() {
  v_textCords = a_textCords;
  gl_Position = u_model_view * a_position;
}
