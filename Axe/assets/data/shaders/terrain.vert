#ifdef GL_ES
precision mediump float;
#endif 

attribute vec4   a_position;
attribute vec3   a_normal;
attribute vec4   a_color;
attribute vec2   a_textCords;

varying vec4  v_color;
varying vec2  v_textCords;

uniform mat4 u_projectionViewMatrix;
uniform vec3 u_light_direction;
uniform vec4 u_light_color;
uniform vec4 u_ambient_color;
void main() {
  vec3 normalized_light_direction = normalize(u_light_direction);

  float contribution   = max(dot(a_normal,normalized_light_direction),0.0);
  v_color              = (u_light_color * contribution) * (a_color * u_ambient_color);
  v_textCords          = a_textCords;
  gl_Position          = u_projectionViewMatrix * a_position;
}