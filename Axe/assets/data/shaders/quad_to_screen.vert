attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoords;

//uniform mat4 u_worldView;
uniform vec2 vec0; //(0, 0) in world coords
uniform vec2 vec1; //(width, height) in world coords

varying vec4 v_color;
varying vec2 v_texCoords;
 
// remember that you should draw a screen aligned quad
void main(void)
{
    v_color = a_color;
    v_texCoords = a_texCoords;
    vec2 pos = sign(a_position.xy);
    if (pos.x == 0)
        pos.x = vec0.x;
    else
        pos.x = vec1.x;
    if (pos.y == 0)        
        pos.y = vec0.y;
    else
        pos.y = vec1.y;  
    //gl_Position =  vec4(pos.x, pos.y, a_position.z, a_position.w);
    gl_Position =  vec4(pos.x, pos.y, 0, 1);
}