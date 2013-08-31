attribute vec4 a_position;
uniform   mat4 u_model_view;
uniform   mat4 u_world_view;

varying   vec3 a_texCoord; 

void main() {
  a_texCoord  = a_position.xyz;
  gl_Position = u_world_view * u_model_view * a_position;
  
}