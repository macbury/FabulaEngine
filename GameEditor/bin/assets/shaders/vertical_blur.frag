uniform sampler2D u_texture; // this should hold the texture rendered by the horizontal blur pass
varying vec4 v_color;
varying vec2 v_texCoords;
 
float blurSize = 1.0/512.0;
 
void main(void)
{
   vec4 sum = vec4(0.0);
   vec2 tmp = gl_FragCoord.xy;
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 4.0*blurSize)) * 0.05;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 3.0*blurSize)) * 0.09;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 2.0*blurSize)) * 0.12;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - blurSize)) * 0.15;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y)) * 0.16;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + blurSize)) * 0.15;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 2.0*blurSize)) * 0.12;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 3.0*blurSize)) * 0.09;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 4.0*blurSize)) * 0.05;
 
   gl_FragColor = sum;
}