package com.thesecretpie.shader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Array;

/**
 * @author Przemek Müller
 * Loads text files using AssetManager.
 *
 */
public class TextFileLoader extends SynchronousAssetLoader<String, TextFileLoader.TextFileParameter> {
	
	public TextFileLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public String load (AssetManager assetManager, String fileName, TextFileParameter parameter) {
		String result = null;
		FileHandle fh = resolve(fileName);
		if (fh.exists()) {
			result = fh.readString("utf-8");
		}
		return result;
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, TextFileParameter parameter) {
		return null;
	}

	static public class TextFileParameter extends AssetLoaderParameters<String> {
	}
}