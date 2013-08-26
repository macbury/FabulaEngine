package com.thesecretpie.shader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.glutils.FloatFrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * @author Przemek Muller
 * Processor is a framebuffer that renders into itself.
 * Think of it as a data container for the GPU - you can run various filters (shaders) to modify its data. 
 * See ProcessorTest for an example.
 *
 */
public class Processor {
	
	public FrameBuffer fbo;
	
	protected ShaderManager sm;
	protected int width, height;
	protected Format format;
	protected boolean saveBytes = false;
	protected ByteBuffer data;
	
	protected static Mesh quad;
	protected Color col = new Color();
	protected ObjectMap<String, Object> uniforms = new ObjectMap<String, Object>();
	protected Vector2 size;
	
	public Processor(ShaderManager sm, int w, int h, boolean hasDepth) {
		this(sm, w, h, null, hasDepth, false);
	}
	
	public Processor(ShaderManager sm, int w, int h, boolean hasDepth, boolean saveBytes) {
		this(sm, w, h, null, hasDepth, saveBytes);
	}
	
	public Processor(ShaderManager sm, int w, int h, Format format, boolean hasDepth, boolean saveBytes) {
		this.sm = sm;
		this.width = w;
		this.height = h;
		this.size = new Vector2(w, h);
		this.format = format;
		if (format == null) {
			//this is a float format
			this.fbo = new FloatFrameBuffer(width, height, hasDepth);
		}
		else {
			this.fbo = new FrameBuffer(format, width, height, hasDepth);
		}
		this.saveBytes = saveBytes;
		if (saveBytes) {
			data = ByteBuffer.allocateDirect(width * height * 4);
		}
		createQuad();
	}
	
	public void setTextureFilter(TextureFilter filter) {
		fbo.getColorBufferTexture().setFilter(filter, filter);
	}
	
	public void setUniform(String name, Object value) {
		uniforms.put(name, value);
	}
	
	public void setUniform(String name, Object... values) {
		uniforms.put(name, values);
	}
	
	protected void addUniforms(ShaderProgram program) {
		for (Entry<String, Object> entry: uniforms.entries()) {
			if (entry.value.getClass().isArray()) {
				addUniformsArray(program, entry.key, (Object[]) entry.value);
				continue;
			}
			else {
				addUniform(program, entry.key, entry.value);
			}
		}
	}
	
	protected void addUniform(ShaderProgram program, String key, Object value) {
		String cls = value.getClass().getName();
		//TODO: change that to hashtable? Check performance first!
		if (cls.equals("java.lang.Integer"))
			program.setUniformi(key, (Integer) value);
		else if (cls.equals("java.lang.Float"))
			program.setUniformf(key, (Float) value);
		else if (cls.equals("com.badlogic.gdx.graphics.Texture")) {
			Texture tex = (Texture) value;
			int texId = sm.getCurrentTextureId();
			tex.bind(texId);
			program.setUniformi(key, texId);
		}
		else if (cls.equals("com.thesecretpie.shader.Processor") 
				|| cls.equals("com.thesecretpie.shader.BufferedProcessor")) {
			Texture tex = ((Processor) value).getResult();
			int texId = sm.getCurrentTextureId();
			tex.bind(texId);
			program.setUniformi(key, texId);
		}
		else if (cls.equals("com.badlogic.gdx.graphics.Color")) {
			Color val = (Color) value;
			program.setUniformf(key, val.r, val.g, val.b, val.a);
		}
		else if (cls.equals("com.badlogic.gdx.math.Vector2")) {
			Vector2 val = (Vector2) value;
			program.setUniformf(key, val.x, val.y);
		}
		else if (cls.equals("com.badlogic.gdx.math.Vector3")) {
			Vector3 val = (Vector3) value;
			program.setUniformf(key, val.x, val.y, val.z);
		}
		else if (cls.equals("com.badlogic.gdx.math.Matrix3")) {
			program.setUniformMatrix(key, (Matrix3) value);
		}
		else if (cls.equals("com.badlogic.gdx.math.Matrix4")) {
			program.setUniformMatrix(key, (Matrix4) value);
		}
		else {
			throw new GdxRuntimeException("Class " + cls + " as uniform: not implemented yet!");
		}
	}
	
	protected void addUniformsArray(ShaderProgram program, String key,
			Object[] values) {
		if (values == null || values.length == 0)
			return;
		Object val = values[0];
		String cls = val.getClass().getName();
		if (cls.equals("java.lang.Integer")) {
			switch (values.length) {
			case 1:
				program.setUniformi(key, (Integer) values[0]);
				break;
			case 2:
				program.setUniformi(key, (Integer) values[0],
						(Integer) values[1]);
				break;
			case 3:
				program.setUniformi(key, (Integer) values[0],
						(Integer) values[1], (Integer) values[2]);
				break;
			case 4:
				program.setUniformi(key, (Integer) values[0],
						(Integer) values[1], (Integer) values[2], (Integer) values[3]);
				break;
			}
		}
		else if (cls.equals("java.lang.Float")) {
			switch (values.length) {
			case 1:
				program.setUniformf(key, (Float) values[0]);
				break;
			case 2:
				program.setUniformf(key, (Float) values[0],
						(Float) values[1]);
				break;
			case 3:
				program.setUniformf(key, (Float) values[0],
						(Float) values[1], (Float) values[2]);
				break;
			case 4:
				program.setUniformf(key, (Float) values[0],
						(Float) values[1], (Float) values[2], (Float) values[3]);
				break;
			}
		}
		else {
			throw new GdxRuntimeException("Class " + cls + " as uniform: not implemented yet!");
		}
	}

	public void run(ShaderProgram program) {
		int texId = sm.getCurrentTextureId();
		fbo.begin();
			program.begin();
			getResult().bind(texId);
			program.setUniformi("u_texture", texId);
			program.setUniformf("u_viewport", width, height);
			addUniforms(program);
			quad.render(program, GL20.GL_TRIANGLES);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
			program.end();
		fbo.end();
	}
	
	public void run(String program) {
		fbo.begin();
			sm.begin(program);
			sm.setUniformTexture("u_texture", getResult());
			sm.setUniformf("u_viewport", width, height);
			addUniforms(sm.getCurrent());
			quad.render(sm.getCurrent(), GL20.GL_TRIANGLES);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
			sm.end();
		fbo.end();
	}
	
	public void blur3() {
		blur(3);
	}
	
	public void blur5() {
		blur(5);
	}
	
	public void blur7() {
		blur(7);
	}
	
	public void blur9() {
		blur(9);
	}
	
	protected void blur(float radius) {
		setUniform("u_radius", radius);
		setUniform("u_axis", 0f, 1f);
		run("processor_blur");
		setUniform("u_axis", 1f, 0f);
		run("processor_blur");
	}
	
	public Texture getResult() {
		return fbo.getColorBufferTexture();
	}
	
	public void copyFrom(Processor other) {
		setUniform("u_source", other.getResult());
		//setUniform("u_source_size", other.getSize());
		run("copy");
	}
	
	public void copyFrom(FrameBuffer other) {
		setUniform("u_source", other.getColorBufferTexture());
		run("copy");
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Vector2 getSize() {
		return size;
	}
	
	public Color getValue(int x, int y) {
		if (saveBytes) {
			int i = (x + (width * y)) * 4;
			int r = data.get(i) & 0xFF;
			int g = data.get(i+1) & 0xFF;
			int b = data.get(i+2) & 0xFF;
			int a = data.get(i+3) & 0xFF;
			col.set(r/255f, g/255f, b/255f, a/255f);
			return col;
		}
		
		fbo.begin();
			sm.begin("processor");
			fbo.getColorBufferTexture().bind(ShaderManager.FRAMEBUFFER_TEXTURE_ID);
			sm.setUniformi("u_texture", ShaderManager.FRAMEBUFFER_TEXTURE_ID);
			sm.setUniformf("u_viewport", width, height);
			quad.render(sm.getCurrent(), GL20.GL_TRIANGLES);
			ByteBuffer data = getFrameBufferPixels(x, y, 1, 1, false, null);
			sm.end();
			
			int r = data.get(0) & 0xFF;
			int g = data.get(1) & 0xFF;
			int b = data.get(2) & 0xFF;
			int a = data.get(3) & 0xFF;
			
			col.set(r/255f, g/255f, b/255f, a/255f);
		fbo.end();
		
		return col;
	}
	
	/**
	 * Renders the framebuffer to entire screen using default shader.
	 */
	public void renderDefault() {
		sm.renderFBDefault(fbo);
	}
	
	/**
	 * Renders the framebuffer to pre-created screen quad. Remember to provide your own shader beforehand!
	 */
	public void render() {
		sm.renderFB(fbo);
	}
	
	/**
	 * Renders the Processor's framebuffer to given framebuffer.
	 */
	public void renderTo(FrameBuffer fb) {
		fb.begin();
		renderDefault();
		fb.end();
	}
	
	public static ByteBuffer getFrameBufferPixels (int x, int y, int w, int h, boolean flipY, ByteBuffer lines) {
		Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);
		//final ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 4);
		final int numBytes = w * h * 4;
		if (lines == null || lines.capacity() == 0)
			lines = ByteBuffer.allocateDirect(numBytes);
		lines.position(0);
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, lines);
		
		//TODO - make it work
		if (flipY) {
			ByteBuffer pixels = ByteBuffer.allocate(numBytes);
			final int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				lines.position((h - i - 1) * numBytesPerLine);
				lines.get((byte[]) pixels.array(), i * numBytesPerLine, numBytesPerLine);
			}
			return pixels;
		} 
		return lines;

	}
	
	private void createQuad() {
		if (quad != null)
			return;
		quad = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 3,
	        "a_position"), new VertexAttribute(Usage.Color, 4, "a_color"),
	        new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

	    quad.setVertices(new float[]{-1, -1, 0, 1, 1, 1, 1, 0, 1,
	                1, -1, 0, 1, 1, 1, 1, 1, 1,
	                1, 1, 0, 1, 1, 1, 1, 1, 0,
	                -1, 1, 0, 1, 1, 1, 1, 0, 0});
		quad.setIndices(new short[]{0, 1, 2, 2, 3, 0});
	    //quad.setIndices(new short[]{1, 0, 2, 3});
	}

	public void dump(FileHandle absolute) {
		//TODO: needs to be flipped on Y axis
		Pixmap pix;
		if (format != null)
			pix = new Pixmap(width, height, format);
		else
			pix = new Pixmap(width, height, Format.RGBA8888);
		ByteBuffer pixels = pix.getPixels();
		
		/*if (saveBytes) {
			pixels.put(data);
		}
		else {*/
			fbo.begin();
				sm.begin("processor");
				sm.setUniformTexture("u_texture", getResult());
				sm.setUniformf("u_viewport", width, height);
				quad.render(sm.getCurrent(), GL20.GL_TRIANGLES);
				getFrameBufferPixels(0, 0, width, height, true, pixels);
				sm.end();
			fbo.end();
		//}
		
		try {
			com.badlogic.gdx.graphics.PixmapIO.writePNG(absolute, pix);
		}
		catch (Exception ex) {
			Gdx.app.log("ShaderManager", "Error while dumping Processor texture: " + ex.getMessage());
		}
	}
	
	public void dump(String filepath) {
		dump(Gdx.files.absolute(filepath));
	}

	public void clear() {
		clear(Color.BLACK);
	}
	
	public void clear(Color col) {
		fbo.begin();
			Gdx.gl20.glClearColor(col.r, col.g, col.b, col.a);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
		fbo.end();
	}
	
}
