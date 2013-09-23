#ifdef GL_ES
precision highp float;
#endif

uniform samplerCube s_cubemap;
uniform sampler2D   u_texture;
uniform float       u_water_alpha;
uniform float       u_water_mix;
varying vec2        v_texCoord;
varying vec3        v_reflection;
varying vec4        v_color;
void main () {
  vec4 texel   = mix(texture2D(u_texture, v_texCoord), textureCube(s_cubemap, v_reflection), u_water_mix);
  texel.a      = u_water_alpha;
  gl_FragColor = texel;
}