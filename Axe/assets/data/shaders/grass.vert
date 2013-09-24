#ifdef GL_ES
  precision highp float;
#endif

attribute vec4 a_position;
attribute vec2 a_textCords;
attribute vec4 a_color;
varying vec2   v_textCords;

uniform mat4   u_model_view;
uniform vec2   u_wave_data;

void main() {
  v_textCords = a_textCords;

  vec4 newPos       = vec4(
    a_position.x + a_color.r * (u_wave_data.y * cos(u_wave_data.x+a_position.z)), 
    a_position.y, 
    a_position.z, 
  a_position.w);

  gl_Position = u_model_view * newPos;
}
