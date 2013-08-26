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
 * It behaves like the regular processor, but uses two framebuffers and renders ping-pong into them.
 *
 */
public class BufferedProcessor extends Processor {
	
	public FrameBuffer fbo2;
	
	public BufferedProcessor(ShaderManager sm, int w, int h, boolean hasDepth) {
		this(sm, w, h, null, hasDepth, false);
	}
	
	public BufferedProcessor(ShaderManager sm, int w, int h, boolean hasDepth, boolean saveBytes) {
		this(sm, w, h, null, hasDepth, saveBytes);
	}
	
	public BufferedProcessor(ShaderManager sm, int w, int h, Format format, boolean hasDepth, boolean saveBytes) {
		super(sm, w, h, format, hasDepth, saveBytes);
		if (format == null) {
			//this is a float format
			this.fbo2 = new FloatFrameBuffer(width, height, hasDepth);
		}
		else {
			this.fbo2 = new FrameBuffer(format, width, height, hasDepth);
		}
	}
	
	protected void swapBuffers() {
		FrameBuffer tmp = fbo;
		fbo = fbo2;
		fbo2 = tmp;
	}
	
	@Override
	public void run(ShaderProgram program) {
		int texId = sm.getCurrentTextureId();
		fbo.begin();
			program.begin();
			fbo2.getColorBufferTexture().bind(texId);
			program.setUniformi("u_texture", sm.getCurrentTextureId());
			program.setUniformf("u_viewport", width, height);
			addUniforms(program);
			quad.render(program, GL20.GL_TRIANGLES);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
			program.end();
		fbo.end();
		swapBuffers();
	}
	
	@Override
	public void run(String program) {
		fbo.begin();
			sm.begin(program);
			sm.setUniformTexture("u_texture", fbo2.getColorBufferTexture());
			sm.setUniformf("u_viewport", width, height);
			addUniforms(sm.getCurrent());
			quad.render(sm.getCurrent(), GL20.GL_TRIANGLES);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
			sm.end();
		fbo.end();
		swapBuffers();
	}
	
	@Override
	public void copyFrom(Processor other) {
		setUniform("u_source", other.getResult());
		//setUniform("u_source_size", other.getSize());
		run("copy");
		swapBuffers();
		run("copy");
	}
	
	@Override
	public Color getValue(int x, int y) {
		if (saveBytes) {
			return super.getValue(x, y);
		}
		
		fbo.begin();
			sm.begin("processor");
			fbo2.getColorBufferTexture().bind(ShaderManager.FRAMEBUFFER_TEXTURE_ID);
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
		swapBuffers();
		
		return col;
	}
	
	@Override
	public void clear(Color col) {
		fbo.begin();
			Gdx.gl20.glClearColor(col.r, col.g, col.b, col.a);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			if (saveBytes)
				data = getFrameBufferPixels(0, 0, width, height, false, data);
		fbo.end();
		
		fbo2.begin();
			Gdx.gl20.glClearColor(col.r, col.g, col.b, col.a);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		fbo2.end();
	}
	
}
