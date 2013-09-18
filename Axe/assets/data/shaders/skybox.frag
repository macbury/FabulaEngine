#ifdef GL_ES
precision highp float;
#endif

uniform samplerCube s_cubemap;

varying vec3 v_texCoord;
 
void main (void) {
    gl_FragColor = textureCube(s_cubemap, v_texCoord);
}