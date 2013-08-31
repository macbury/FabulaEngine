uniform sampler2D u_texture;
varying vec4 v_color;
varying vec2 v_texCoords;

void main()
{
   vec4 sum = vec4(0);
   //vec2 texcoord = vec2(gl_TexCoord[0]);
   vec2 texcoord = v_texCoords;
   int j;
   int i;

   for( i= -4 ;i < 4; i++)
   {
        for (j = -3; j < 3; j++)
        {
            sum += texture2D(u_texture, texcoord + vec2(j, i)*0.008) * 0.15;
        }
   }
       if (texture2D(u_texture, texcoord).r < 0.3)
    {
       gl_FragColor = sum*sum*0.002 + texture2D(u_texture, texcoord);
    }
    else
    {
        if (texture2D(u_texture, texcoord).r < 0.05)
        {
            gl_FragColor = sum*sum*0.003 + texture2D(u_texture, texcoord);
        }
        else
        {
            gl_FragColor = sum*sum*0.0055 + texture2D(u_texture, texcoord);
        }
    }
}