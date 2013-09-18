#ifdef GL_ES
precision highp float; 
#endif

attribute vec4 a_position;

uniform mat4 u_mvpMatrix;

varying vec3 v_texCoord;

void main() {
    v_texCoord = a_position.xyz;
    gl_Position = u_mvpMatrix * a_position;
}
