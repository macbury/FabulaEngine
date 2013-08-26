attribute vec4 a_position;
attribute vec4 a_color;

uniform mat4 u_worldView;

varying vec4 v_color;
varying vec2 v_texCoords;
			
void main() {
    v_color = a_color;
    gl_Position =  a_position * u_worldView;
}