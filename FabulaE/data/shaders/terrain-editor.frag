#ifdef GL_ES
precision mediump float;
#endif 
varying vec4  v_color;
varying vec2  v_textCords;
varying float v_textureNumber;
varying vec2  v_tile_position;

uniform sampler2D u_texture0;
uniform vec2      u_brush_position;
uniform float     u_brush_size;
uniform float     u_wireframe;
void main() {
  if (u_wireframe == 1.0) {
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  } else {
    vec4 current_texture = texture2D(u_texture0, v_textCords);
    float dist           = length(round(u_brush_position) - round(v_tile_position));
    if (dist <= u_brush_size && u_brush_size > 0.0f) {
    gl_FragColor = vec4(1.5f, 1.5f, 1.5f, 0.5f) * current_texture;
    } else {
        gl_FragColor = current_texture;
    }
  }
}