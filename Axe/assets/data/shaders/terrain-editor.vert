#ifdef GL_ES
precision mediump float;
#endif 

attribute vec4   a_position;
attribute vec2   a_textCords;
attribute vec2   a_tile_position;

varying vec2  v_textCords;
varying vec2  v_tile_position;
varying vec4  v_color;
varying vec2  v_position;


uniform vec4 u_ambient_color;
uniform mat4 u_projectionViewMatrix;
uniform vec3 u_light_direction;
uniform vec4 u_light_color;
uniform float layer_style;

void main() {
  v_color = vec4(1.0,1.0,1.0,1.0);
  
  if (((int)a_tile_position.x % 2 == 0) && ((int)a_tile_position.y % 2) != 0) {
    v_color *= vec4(0.9f,0.9f,0.9f,1f);
  } else if (((int)a_tile_position.x % 2) != 0 && ((int)a_tile_position.y % 2) == 0) {
    v_color *= vec4(0.9f,0.9f,0.9f,1f);
  }
  v_position      = mod(a_position.xy, vec2(2f, 2f));
  v_textCords     = a_textCords;
  v_tile_position = a_tile_position;


  gl_Position     = u_projectionViewMatrix * a_position;
}