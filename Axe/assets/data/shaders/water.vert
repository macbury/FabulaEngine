#ifdef GL_ES
precision highp float; 
#endif

attribute vec4 a_position;
attribute vec2 a_textCords;
attribute vec3 a_normal;

uniform vec2 u_wave_data;
uniform mat4 u_model_view;
uniform vec3 u_camera_position;
uniform vec4 u_texture_cordinates;

varying vec2 v_texCoord;
varying vec3 v_reflection;


void main() {
  vec4 newPos       = vec4(
    a_position.x + u_wave_data.y * sin(u_wave_data.x+a_position.x+a_position.y), 
    a_position.y + u_wave_data.y * cos(u_wave_data.x+a_position.x+a_position.y), 
    a_position.z, 
  a_position.w);
  
  vec3 eye_direction = normalize(newPos.xyz - u_camera_position);
  v_reflection       = reflect(eye_direction, a_normal);
  
  v_texCoord = vec2(0,0);
  
  if (round(a_textCords.x) == 1.0) {
    v_texCoord.x = u_texture_cordinates.z;
  } else {
    v_texCoord.x = u_texture_cordinates.x;
  }
  
  if (round(a_textCords.y) == 1.0) {
    v_texCoord.y = u_texture_cordinates.w;
  } else {
    v_texCoord.y = u_texture_cordinates.y;
  }
  
  gl_Position        = u_model_view * newPos;
}
