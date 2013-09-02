#ifdef GL_ES
precision mediump float;
#endif 

attribute vec4   a_position;
attribute vec3   a_normal;
attribute vec4   a_color;
attribute vec2   a_textCords;
attribute vec2   a_tile_position;

varying vec4  v_color;
varying vec2  v_textCords;
varying vec2  v_tile_position;
varying vec3  v_normal;

uniform vec4 u_ambient_color;
uniform mat4 u_projectionViewMatrix;
uniform vec3 u_light_direction;
uniform vec4 u_light_color;

void main() {
  if ((int)a_tile_position.x % 2 == 0 && (int)a_tile_position.y % 2 != 0) {
    a_color = vec4(0.9,0.9,0.9,1) * a_color;
  } else if ((int)a_tile_position.x % 2 != 0 && (int)a_tile_position.y % 2 == 0) {
    a_color = vec4(0.9,0.9,0.9,1) * a_color;
  }
  
  v_normal        = a_normal;
  v_color         = (a_color * u_ambient_color);
  v_textCords     = a_textCords;
  v_tile_position = a_tile_position;
  gl_Position     = u_projectionViewMatrix * a_position;
}