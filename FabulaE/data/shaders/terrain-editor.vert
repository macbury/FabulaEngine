attribute vec4   a_position;
attribute vec3   a_normal;
attribute vec4   a_color;
attribute vec2   a_textCords;
attribute vec2   a_tile_position;

varying vec4  v_color;
varying vec2  v_textCords;
varying vec2  v_tile_position;

uniform vec4 u_ambient_color;
uniform mat4 u_projectionViewMatrix;
uniform vec3 u_light_direction;
uniform vec4 u_light_color;

void main() {
  vec3 lightNorm    = normalize(u_light_direction);
  float lightWeight = max(dot(a_normal,lightNorm),0.0);
  
  v_color         = (u_light_color * lightWeight) * (a_color * u_ambient_color);
  v_textCords     = a_textCords;
  v_tile_position = a_tile_position;
  
  gl_Position     = u_projectionViewMatrix * a_position;
}