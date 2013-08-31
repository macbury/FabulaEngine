#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_texCoord;
uniform sampler2D u_texture1;

void main() {
  gl_FragColor = sampler2D(u_texture0, v_texCoord);
}