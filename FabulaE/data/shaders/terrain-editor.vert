attribute vec4   a_position;
attribute vec4   a_normal;
attribute vec4   a_color;
attribute vec2   a_textCords;
attribute vec2   a_tile_position;

varying vec4  v_color;
varying vec2  v_textCords;
varying vec2  v_tile_position;

uniform vec4 u_ambient_color;
uniform mat4 u_projectionViewMatrix;

void main() {
  v_color         = a_color * u_ambient_color;
  v_textCords     = a_textCords;
  v_tile_position = a_tile_position;
  
  gl_Position     = u_projectionViewMatrix * a_position;
}