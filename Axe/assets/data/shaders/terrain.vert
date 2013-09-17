#ifdef GL_ES
precision mediump float;
#endif 

attribute vec4   a_position;
attribute vec2   a_textCords;

varying vec2  v_textCords;

uniform mat4 u_projectionViewMatrix;
uniform vec3 u_light_direction;
uniform vec4 u_light_color;
uniform vec4 u_ambient_color;
void main() {
  v_textCords          = a_textCords;
  gl_Position          = u_projectionViewMatrix * a_position;
}