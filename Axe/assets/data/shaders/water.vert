#ifdef GL_ES
precision highp float;
#endif

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;

uniform vec2 u_wave_data;
uniform mat4 u_model_view;
uniform vec3 u_camera_position;
uniform vec4 u_texture_cordinates;

varying vec2 v_texCoord;
varying vec3 v_reflection;
varying vec4 v_color;

void main() {
  vec4 newPos       = vec4(
    a_position.x + u_wave_data.y * sin(u_wave_data.x+a_position.x+a_position.y), 
    a_position.y + u_wave_data.y * cos(u_wave_data.x+a_position.x+a_position.y), 
    a_position.z + u_wave_data.y * cos(u_wave_data.x+a_position.y+a_position.z), 
  a_position.w);
  
  vec3 eye_direction = normalize(newPos.xyz - u_camera_position);
  v_reflection       = reflect(eye_direction, a_normal);
  v_color            = a_color;
  if (a_color.r > 0.0) {
    v_texCoord = vec2(u_texture_cordinates.z,u_texture_cordinates.y);
  } else if (a_color.g > 0.0) {
    v_texCoord = vec2(u_texture_cordinates.x,u_texture_cordinates.y);
  } else if (a_color.b > 0.0) {
    v_texCoord = vec2(u_texture_cordinates.z,u_texture_cordinates.w);
  } else {
    v_texCoord = vec2(u_texture_cordinates.x,u_texture_cordinates.w);
  }
  
  gl_Position        = u_model_view * newPos;
}
