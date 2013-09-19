#ifdef GL_ES
precision mediump float;
#endif 

attribute vec2   a_textCords;

varying vec2  v_textCords;
varying vec2  v_tile_position;
varying vec4  v_color;

varying vec2  v_position;

uniform sampler2D u_texture0;
uniform vec2      u_brush_position;
uniform float     u_brush_size;
uniform vec3      u_light_direction;
uniform vec4      u_light_color;
uniform mat4      u_projectionViewMatrix;
uniform vec4      u_ambient_color;
uniform int       u_brush_type;
uniform vec2      u_brush_start_position;

bool isInRect() {
  if (u_brush_type == 1) {
    float sx = round(min(u_brush_start_position.x, u_brush_position.x)); 
    float sy = round(min(u_brush_start_position.y, u_brush_position.y));
    float ex = round(max(u_brush_start_position.x, u_brush_position.x)); 
    float ey = round(max(u_brush_start_position.y, u_brush_position.y));
    
    return (round(v_tile_position.x) >= sx && round(v_tile_position.y) >= sy && round(v_tile_position.x) <= ex && round(v_tile_position.y) <= ey);
  } else {
    return (round(u_brush_position.x - u_brush_size) <= round(v_tile_position.x) && round(u_brush_position.x + u_brush_size) >= round(v_tile_position.x)) && (round(u_brush_position.y - u_brush_size) <= round(v_tile_position.y) && round(u_brush_position.y + u_brush_size) >= round(v_tile_position.y));
  }
}

void main() {
  //v_color   = vec4(0,0,0,1);
  //v_color.r = clamp(v_textCords.y, 0.0, 1.03);
  //if (mod(v_color.b, 0.2) == 0.0) {
    //v_color   = vec4(1,1,1,1);
  //}
  //v_color.r = glFragCord.x;
  //v_color   = clamp(v_color, 0;, 1.0);
  vec4 current_texture = v_color * texture2D(u_texture0, v_textCords);
  
  if (isInRect()) {
    gl_FragColor = vec4(1.5f, 1.5f, 1.5f, 0.5f) * current_texture;
  } else {
    gl_FragColor = current_texture;
  }
}