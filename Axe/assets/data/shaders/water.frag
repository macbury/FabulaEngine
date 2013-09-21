#ifdef GL_ES
precision highp float;
#endif

uniform samplerCube s_cubemap;

varying vec3 v_texCoord;
varying vec3 v_reflection;
void main (void) {
  gl_FragColor = textureCube(s_cubemap, v_reflection);
  gl_FragColor.a = 0.7;
}