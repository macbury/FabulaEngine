#ifdef GL_ES
precision mediump float;
#endif 

attribute vec4   a_position;
attribute vec3   a_normal;
attribute vec4   a_color;
attribute vec2   a_textCords;

varying vec4  v_color;
varying vec2  v_textCords;
varying vec3  v_normal;

uniform mat4 u_projectionViewMatrix;
uniform vec4 u_ambient_color;

void main() {
  v_normal        = a_normal;
  v_color         = (a_color * u_ambient_color);
  v_textCords     = a_textCords;
  gl_Position     = u_projectionViewMatrix * a_position;
}