uniform sampler2D u_texture;
uniform vec2 u_viewport;
uniform vec4 u_brush_col;
uniform vec2 u_brush_pos;
uniform vec2 u_prev_brush_pos;
uniform float u_brush_size;

varying vec2 v_position;
varying vec4 v_color;
varying vec2 v_texCoords;

vec4 draw_brush(vec2 pos) {
	vec2 uv = gl_FragCoord.xy/u_viewport;
    float dist = distance(pos, gl_FragCoord.xy)/u_brush_size;
    float t = clamp(-pow(dist, 2.0) + 1.0, 0.0, 1.0);// * u_brush_size;
    //t = 1.0 / distance(pos, gl_FragCoord.xy) * 1.0;
    vec4 col = u_brush_col;
    col.a = t; // t/brush_size;
    return mix(col, texture2D(u_texture, uv), (1.0-col.a));
    
    //vec4 col = u_brush_col;
    //col.a = t;
    //gl_FragColor = col + texture2D(u_texture, uv);
}
    
void main(void){
    
    vec2 uv = gl_FragCoord.xy/u_viewport;
    if (u_brush_pos.x == -99.0) {
        //not drawing
        gl_FragColor = texture2D(u_texture, uv);
    }
    else {
        if (u_prev_brush_pos.x == -99.0 || u_prev_brush_pos == u_brush_pos) {
            gl_FragColor = draw_brush(u_brush_pos);
        }
        else {
            //TODO connect 2 points
            //ograniczyc do recta miedzy (x1, y1) a (x2, y2) 
            float x1 = u_prev_brush_pos.x;
            float y1 = u_prev_brush_pos.y; 
            float x2 = u_brush_pos.x;
            float y2 = u_brush_pos.y;
            float xt = gl_FragCoord.x;
            float yt = gl_FragCoord.y;
            
            float minx = min(x1, x2);
            float maxx = max(x1, x2);
            float miny = min(y1, y2);
            float maxy = max(y1, y2);
            
            float brfactor = u_brush_size / 2.0;
            if (xt < minx - brfactor
            	|| xt > maxx + brfactor
            	|| yt < miny - brfactor
            	|| yt > maxy + brfactor) {
            	
            	gl_FragColor = texture2D(u_texture, uv);
            }
            else {
            	vec2 closest = vec2(0.0, 0.0);
            	if (x2 == x1) {
            		closest.x = x1;
            		closest.y = yt;
            	}
            	else if (y2 == y1) {
            		closest.x = xt;
            		closest.y = y1;
            	}
            	else {
		            float yxtmp = (y2-y1)/(x2-x1);
		            float xytmp = (x2-x1)/(y2-y1);
		            //factor for line perpendicular to (x2-x1)(y-y1)=(y2-y1)(x-x1)
		            float b = yt + xytmp *xt;
		            //closest point to gl_FragCoord on (x2-x1)(y-y1)=(y2-y1)(x-x1)
		            closest.x = (b + yxtmp * x1 - y1) / (yxtmp + xytmp);
		            closest.y = -xytmp * closest.x + b;
		        }
	            
	            gl_FragColor = draw_brush(closest);
	        }
        }
    }
}
