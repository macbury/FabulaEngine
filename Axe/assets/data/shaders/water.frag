#ifdef GL_ES
precision highp float;
#endif

uniform samplerCube s_cubemap;
uniform sampler2D   u_texture;

varying vec2 v_texCoord;
varying vec3 v_reflection;
void main (void) {

  gl_FragColor = mix(texture2D(u_texture, v_texCoord), textureCube(s_cubemap, v_reflection), 0.7f);
  gl_FragColor.a = 1.0;
}