#ifdef GL_ES
precision mediump float;
#endif 

varying vec4  v_color;
varying vec3  v_normal;
varying vec2  v_textCords;
varying vec2  v_tile_position;

uniform sampler2D u_texture0;
uniform vec2      u_brush_position;
uniform float     u_brush_size;
uniform vec3      u_light_direction;
uniform vec4      u_light_color;
uniform mat4      u_projectionViewMatrix;
uniform vec4      u_ambient_color;

void main() {
  vec3 normalized_light_direction = normalize(u_light_direction);
  float contribution   = max(dot(v_normal,normalized_light_direction),0.0);
  v_color              = (u_light_color * contribution) * v_color;
  vec4 current_texture = v_color * texture2D(u_texture0, v_textCords);
  
  if ((round(u_brush_position.x - u_brush_size) <= round(v_tile_position.x) && round(u_brush_position.x + u_brush_size) >= round(v_tile_position.x)) && (round(u_brush_position.y - u_brush_size) <= round(v_tile_position.y) && round(u_brush_position.y + u_brush_size) >= round(v_tile_position.y))) {
    gl_FragColor = vec4(1.5f, 1.5f, 1.5f, 0.5f) * current_texture;
  } else {
    gl_FragColor = current_texture;
  }
}