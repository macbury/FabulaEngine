#ifdef GL_ES
precision mediump float;
#endif 

varying vec4  v_color;
varying vec3  v_normal;
varying vec2  v_textCords;

uniform sampler2D u_texture0;
uniform vec3      u_light_direction;
uniform vec4      u_light_color;
uniform mat4      u_projectionViewMatrix;
uniform vec4      u_ambient_color;

void main() {
  vec3 normalized_light_direction = normalize(u_light_direction);

  float contribution   = max(dot(v_normal,normalized_light_direction),0.0);
  vec4 final_color     = (u_light_color * contribution) * v_color;
  gl_FragColor         = final_color * texture2D(u_texture0, v_textCords);
}