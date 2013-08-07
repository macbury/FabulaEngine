attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_textCords;
varying vec4 v_color;
varying vec2 v_textCords;
uniform mat4 u_projectionViewMatrix;

void main() {
  v_color     = a_color;
  v_textCords = a_textCords;
  gl_Position = u_projectionViewMatrix * a_position;
}