#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D   u_texture;
varying vec2        v_textCords;
varying vec4        v_color;
void main () {
  vec4 texel = texture2D(u_texture, v_textCords);
  if (texel.a <= 0.0) {
    discard;
  }
  gl_FragColor = texel;
}